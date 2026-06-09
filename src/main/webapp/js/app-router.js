document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const mainContent = document.getElementById("main-content");
    let activeRequest;
    let currentRouteKey = "home";

    function parseHash() {
        const rawHash = window.location.hash.replace(/^#/, "");
        if (!rawHash) {
            return {page: "home", params: new URLSearchParams()};
        }
        const separator = rawHash.indexOf("?");
        const page = separator === -1
            ? rawHash
            : rawHash.substring(0, separator);
        const query = separator === -1
            ? ""
            : rawHash.substring(separator + 1);
        return {
            page: page || "home",
            params: new URLSearchParams(query)
        };
    }

    function routeKey(route) {
        const query = route.params.toString();
        return route.page + (query ? "?" + query : "");
    }

    function buildHash(page, params) {
        const query = params.toString();
        return "#" + page + (query ? "?" + query : "");
    }

    function updateActiveNav(page) {
        document.querySelectorAll(".side-nav .nav-item").forEach(function (item) {
            item.classList.remove("active");
        });
        const active = document.querySelector(
            '.side-nav .nav-item[data-route="' + page + '"]:not([data-tab])'
        );
        active?.classList.add("active");
    }

    async function loadContent(page, params) {
        if (!mainContent) {
            return;
        }
        activeRequest?.abort();
        const requestController = new AbortController();
        activeRequest = requestController;
        const query = new URLSearchParams(params);
        query.set("page", page);
        mainContent.classList.add("is-loading");
        mainContent.setAttribute("aria-busy", "true");

        try {
            const response = await fetch(
                contextPath + "/content?" + query.toString(),
                {
                    headers: {"X-Requested-With": "XMLHttpRequest"},
                    signal: requestController.signal
                }
            );
            const html = await response.text();
            if (!response.ok) {
                throw new Error(response.status === 404
                    ? "未找到该模块"
                    : "内容加载失败");
            }
            mainContent.innerHTML = html;
            mainContent.classList.remove("fade-in");
            window.requestAnimationFrame(function () {
                mainContent.classList.add("fade-in");
            });
            mainContent.querySelectorAll("textarea").forEach(function (textarea) {
                window.resizeTextarea?.(textarea);
            });
        } catch (error) {
            if (error.name === "AbortError") {
                return;
            }
            mainContent.innerHTML =
                '<section class="empty-state card">' +
                "<h2>内容加载失败</h2>" +
                "<p>请稍后重试，或切换到其他模块。</p>" +
                "</section>";
        } finally {
            if (activeRequest === requestController) {
                mainContent.classList.remove("is-loading");
                mainContent.removeAttribute("aria-busy");
            }
        }
    }

    function handleRoute(forceLoad) {
        const route = parseHash();
        const key = routeKey(route);
        updateActiveNav(route.page);
        if (forceLoad || key !== currentRouteKey) {
            currentRouteKey = key;
            loadContent(route.page, route.params);
        }
    }

    function navigate(hash) {
        if (window.location.hash === hash) {
            handleRoute(true);
            return;
        }
        window.location.hash = hash;
    }

    document.addEventListener("click", function (event) {
        const link = event.target.closest("[data-route]");
        if (!link) {
            return;
        }
        event.preventDefault();
        const page = link.dataset.route;
        const params = new URLSearchParams();
        if (link.dataset.tab) {
            params.set("tab", link.dataset.tab);
        }
        navigate(buildHash(page, params));
    });

    document.addEventListener("submit", function (event) {
        const squareSearch = event.target.closest("[data-square-search]");
        const globalSearch = event.target.closest("[data-global-search]");
        if (!squareSearch && !globalSearch) {
            return;
        }
        event.preventDefault();
        const form = squareSearch || globalSearch;
        const keyword = new FormData(form).get("q")?.toString().trim() || "";
        const params = new URLSearchParams();
        const current = parseHash();
        params.set(
            "tab",
            squareSearch && current.page === "square"
                ? current.params.get("tab") || "latest"
                : "latest"
        );
        if (keyword) {
            params.set("q", keyword);
        }
        navigate(buildHash("square", params));
    });

    window.addEventListener("hashchange", function () {
        handleRoute(false);
    });

    if (!window.location.hash) {
        window.history.replaceState(
            null,
            "",
            window.location.pathname + window.location.search + "#home"
        );
        updateActiveNav("home");
    } else {
        const initialRoute = parseHash();
        updateActiveNav(initialRoute.page);
        if (routeKey(initialRoute) !== "home") {
            currentRouteKey = routeKey(initialRoute);
            loadContent(initialRoute.page, initialRoute.params);
        }
    }
});
