document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const mainContent = document.getElementById("main-content");
    const globalSearchInput = document.querySelector(
        "[data-global-search] input[name='keyword']"
    );
    let activeRequest;
    let currentRouteKey = "home";

    function initCampusMapPreview() {
        const modal = document.querySelector("[data-campus-map-modal]");
        if (!modal || document.documentElement.dataset.campusMapBound === "true") {
            return;
        }
        document.documentElement.dataset.campusMapBound = "true";
        let activeOpener = null;

        const closeMap = function () {
            if (modal.hidden) {
                return;
            }
            modal.hidden = true;
            document.body.classList.remove("campus-map-open");
            activeOpener?.focus();
            activeOpener = null;
        };

        document.addEventListener("click", function (event) {
            const openButton = event.target.closest("[data-campus-map-open]");
            if (openButton) {
                event.preventDefault();
                event.stopPropagation();
                activeOpener = openButton;
                modal.hidden = false;
                document.body.classList.add("campus-map-open");
                modal.querySelector(".campus-map-close")?.focus();
                return;
            }

            const closeButton = event.target.closest("[data-campus-map-close]");
            if (closeButton && modal.contains(closeButton)) {
                event.preventDefault();
                closeMap();
            }
        });

        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape" && !modal.hidden) {
                closeMap();
            }
        });
    }

    window.initCampusMapPreview = initCampusMapPreview;

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
        if (page === "profile" && !query.has("tab")) {
            query.set("tab", "overview");
        }
        if (page === "messages" && !query.has("tab")) {
            query.set("tab", "all");
        }
        if (page === "lostfound") {
            if (!query.has("type")) {
                query.set("type", "all");
            }
            if (!query.has("status")) {
                query.set("status", "all");
            }
        }
        if (page === "activity") {
            if (!query.has("status")) {
                query.set("status", "all");
            }
            if (!query.has("sort")) {
                query.set("sort", "latest");
            }
        }
        query.set("page", page === "my-goods" ? "myGoods" : page);
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
            initCampusMapPreview();
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
        if (route.page === "search" && globalSearchInput) {
            globalSearchInput.value = route.params.get("keyword") || "";
        }
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

    window.CampusHubRouter = {
        parseHash: parseHash,
        navigate: navigate,
        reload: function () {
            handleRoute(true);
        }
    };

    document.addEventListener("click", function (event) {
        const searchTypeLink = event.target.closest("[data-search-type]");
        if (searchTypeLink) {
            event.preventDefault();
            const current = parseHash();
            const keyword = current.params.get("keyword") || "";
            const params = new URLSearchParams();
            params.set("keyword", keyword);
            params.set("type", searchTypeLink.dataset.searchType || "all");
            navigate(buildHash("search", params));
            return;
        }
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
        const searchPageSearch = event.target.closest("[data-search-page-search]");
        if (!squareSearch && !globalSearch && !searchPageSearch) {
            return;
        }
        event.preventDefault();
        const form = squareSearch || globalSearch || searchPageSearch;
        const formData = new FormData(form);
        const keyword = (
            formData.get("keyword") || formData.get("q") || ""
        ).toString().trim();
        if ((globalSearch || searchPageSearch) && !keyword) {
            window.alert("请输入搜索关键词");
            form.querySelector("input[type='search']")?.focus();
            return;
        }
        const params = new URLSearchParams();
        const current = parseHash();
        if (globalSearch || searchPageSearch) {
            params.set("keyword", keyword);
            params.set("type", "all");
            navigate(buildHash("search", params));
            return;
        }
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

    document.addEventListener("keydown", function (event) {
        if ((event.ctrlKey || event.metaKey)
                && event.key.toLowerCase() === "k") {
            event.preventDefault();
            globalSearchInput?.focus();
            globalSearchInput?.select();
        }
    });

    window.addEventListener("hashchange", function () {
        handleRoute(false);
    });

    initCampusMapPreview();

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
