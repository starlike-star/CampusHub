// 处理站内通知的已读、全部已读和未读数量刷新。
document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";

    function updateBadge(count, systemCount, privateCount) {
        const badge = document.querySelector("[data-unread-badge]");
        if (!badge) {
            return;
        }
        const normalized = Math.max(Number(count) || 0, 0);
        badge.textContent = normalized > 99 ? "99+" : String(normalized);
        badge.hidden = normalized === 0;
        const pageCount = document.querySelector("[data-message-page-unread]");
        if (pageCount) {
            pageCount.textContent = String(
                Math.max(Number(systemCount) || 0, 0)
            );
        }
        const markAll = document.querySelector("[data-message-read-all]");
        if (markAll) {
            markAll.disabled = Math.max(Number(systemCount) || 0, 0) === 0;
        }
        const privateUnread = document.querySelector(
            "[data-private-unread-count]"
        );
        if (privateUnread) {
            const normalizedPrivate =
                Math.max(Number(privateCount) || 0, 0);
            privateUnread.textContent = normalizedPrivate > 0
                ? "(" + normalizedPrivate + ")"
                : "";
        }
    }

    async function request(path, options) {
        const response = await fetch(contextPath + path, options);
        const result = await response.json();
        if (result.needLogin) {
            updateBadge(0);
            return null;
        }
        if (!response.ok || !result.success) {
            throw new Error(result.message || "操作失败");
        }
        return result;
    }

    async function refreshUnreadCount() {
        const badge = document.querySelector("[data-unread-badge]");
        if (!badge) {
            return;
        }
        try {
            const result = await request("/messages/unread-count", {
                headers: {"X-Requested-With": "XMLHttpRequest"}
            });
            if (result) {
                updateBadge(
                    result.unreadCount,
                    result.systemUnreadCount,
                    result.privateUnreadCount
                );
            }
        } catch (error) {
            updateBadge(0);
        }
    }

    window.refreshUnreadCount = refreshUnreadCount;

    document.addEventListener("click", async function (event) {
        const readButton = event.target.closest("[data-message-read]");
        if (readButton) {
            event.preventDefault();
            event.stopPropagation();
            const card = readButton.closest("[data-message-card]");
            readButton.disabled = true;
            try {
                const body = new URLSearchParams({
                    messageId: card.dataset.messageId
                });
                const result = await request("/messages/read", {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded;charset=UTF-8"
                    },
                    body: body.toString()
                });
                if (!result) {
                    return;
                }
                card.classList.remove("message-unread");
                card.classList.add("message-read");
                readButton.remove();
                updateBadge(
                    result.unreadCount,
                    result.systemUnreadCount,
                    result.privateUnreadCount
                );
            } catch (error) {
                readButton.disabled = false;
                window.alert(error.message);
            }
            return;
        }

        const readAllButton = event.target.closest("[data-message-read-all]");
        if (!readAllButton) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        readAllButton.disabled = true;
        try {
            const body = new URLSearchParams();
            if (readAllButton.dataset.messageType) {
                body.set("type", readAllButton.dataset.messageType);
            }
            const result = await request("/messages/read-all", {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: body.toString()
            });
            if (!result) {
                return;
            }
            document.querySelectorAll(".message-unread").forEach(function (card) {
                card.classList.remove("message-unread");
                card.classList.add("message-read");
                card.querySelector("[data-message-read]")?.remove();
            });
            updateBadge(
                result.unreadCount,
                result.systemUnreadCount,
                result.privateUnreadCount
            );
        } catch (error) {
            readAllButton.disabled = false;
            window.alert(error.message);
        }
    });

    refreshUnreadCount();
    window.setInterval(refreshUnreadCount, 30000);
});
