document.addEventListener("DOMContentLoaded", function () {
    const publishButton = document.getElementById("publishMenuButton");
    const publishMenu = document.getElementById("publishMenu");
    const accountButton = document.getElementById("accountMenuButton");
    const accountMenu = document.getElementById("accountMenu");
    const checkinButton = document.getElementById("checkinButton");
    const checkinStatus = document.getElementById("checkinStatus");
    const streakDays = document.getElementById("streakDays");
    const checkinProgress = document.getElementById("checkinProgress");
    const toast = document.getElementById("toast");
    let toastTimer;

    function showToast(message) {
        toast.textContent = message;
        toast.classList.add("show");
        window.clearTimeout(toastTimer);
        toastTimer = window.setTimeout(function () {
            toast.classList.remove("show");
        }, 2200);
    }

    // 顶部发布菜单
    publishButton.addEventListener("click", function (event) {
        event.stopPropagation();
        const isOpen = publishMenu.classList.toggle("open");
        publishButton.setAttribute("aria-expanded", String(isOpen));
    });

    publishMenu.addEventListener("click", function (event) {
        const menuItem = event.target.closest("button");
        if (!menuItem) {
            return;
        }
        publishMenu.classList.remove("open");
        publishButton.setAttribute("aria-expanded", "false");
        showToast("已选择：" + menuItem.textContent.trim());
    });

    document.addEventListener("click", function (event) {
        if (!event.target.closest(".publish-wrap")) {
            publishMenu.classList.remove("open");
            publishButton.setAttribute("aria-expanded", "false");
        }
        if (accountMenu && !event.target.closest(".account-wrap")) {
            accountMenu.classList.remove("open");
            accountButton.setAttribute("aria-expanded", "false");
        }
    });

    if (accountButton && accountMenu) {
        accountButton.addEventListener("click", function (event) {
            event.stopPropagation();
            const isOpen = accountMenu.classList.toggle("open");
            accountButton.setAttribute("aria-expanded", String(isOpen));
        });
    }

    // 每日签到静态交互，后续可替换为 Servlet 请求
    checkinButton.addEventListener("click", function () {
        if (checkinButton.classList.contains("checked")) {
            return;
        }
        checkinButton.classList.add("checked");
        checkinButton.textContent = "✓ 已签到";
        checkinButton.disabled = true;
        checkinStatus.textContent = "今日已签到";
        streakDays.textContent = "4 天";
        checkinProgress.style.width = "57%";
        showToast("签到成功，积分 +5");
    });

    // 点赞状态切换与模拟计数
    document.querySelectorAll(".like-button").forEach(function (button) {
        button.addEventListener("click", function () {
            const countElement = button.querySelector("span");
            const originalCount = Number(button.dataset.count);
            const liked = button.classList.toggle("liked");
            countElement.textContent = String(liked ? originalCount + 1 : originalCount);
            button.setAttribute("aria-pressed", String(liked));
        });
    });

    document.querySelectorAll(".bookmark-button").forEach(function (button) {
        button.addEventListener("click", function () {
            const saved = button.classList.toggle("saved");
            button.querySelector("span").textContent = saved ? "已收藏" : "收藏";
            showToast(saved ? "已加入收藏" : "已取消收藏");
        });
    });

    // 信息流标签页仅切换前端选中状态
    document.querySelectorAll(".feed-tab").forEach(function (tab) {
        tab.addEventListener("click", function () {
            document.querySelectorAll(".feed-tab").forEach(function (item) {
                item.classList.remove("active");
            });
            tab.classList.add("active");
        });
    });

    // Ctrl/Cmd + K 聚焦顶部搜索框
    document.addEventListener("keydown", function (event) {
        if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "k") {
            event.preventDefault();
            document.querySelector(".global-search input").focus();
        }

        if (event.key === "Escape") {
            publishMenu.classList.remove("open");
            publishButton.setAttribute("aria-expanded", "false");
            if (accountMenu) {
                accountMenu.classList.remove("open");
                accountButton.setAttribute("aria-expanded", "false");
            }
        }
    });
});
