// 处理个人资料编辑、头像上传、签到及个人内容操作。
document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    let toastTimer;

    function stopInteraction(event) {
        event.preventDefault();
        event.stopPropagation();
    }

    function showToast(message, isError) {
        const toast = document.getElementById("toast");
        if (!toast) {
            window.alert(message);
            return;
        }
        toast.textContent = message;
        toast.classList.toggle("error", Boolean(isError));
        toast.classList.add("show");
        window.clearTimeout(toastTimer);
        toastTimer = window.setTimeout(function () {
            toast.classList.remove("show");
        }, 2400);
    }

    function setBusy(button, busy) {
        button.disabled = busy;
        button.classList.toggle("is-loading", busy);
    }

    function updateCurrentUserAvatars(avatar, nickname) {
        document.querySelectorAll("[data-current-user-avatar]")
            .forEach(function (container) {
                container.replaceChildren();
                if (avatar) {
                    const image = document.createElement("img");
                    image.src = contextPath
                        + (avatar.startsWith("/") ? "" : "/")
                        + avatar;
                    image.alt = nickname || "";
                    container.appendChild(image);
                    return;
                }
                container.textContent = (nickname || "U").slice(0, 1);
            });
    }

    async function postForm(path, values) {
        const body = new URLSearchParams();
        Object.entries(values).forEach(function ([key, value]) {
            body.set(key, value == null ? "" : String(value));
        });
        const response = await fetch(contextPath + path, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: body.toString()
        });
        let result;
        try {
            result = await response.json();
        } catch (error) {
            throw new Error("服务器返回了无效响应");
        }
        if (result.needLogin) {
            window.location.assign(contextPath + "/login");
            return null;
        }
        if (!response.ok || !result.success) {
            throw new Error(result.message || "操作失败");
        }
        return result;
    }

    function currentModal() {
        return document.querySelector("[data-profile-modal]");
    }

    function closeModal() {
        const modal = currentModal();
        if (modal) {
            modal.hidden = true;
        }
        document.body.classList.remove("modal-open");
    }

    function closeAccountCancelModal() {
        const modal = document.querySelector("[data-account-cancel-modal]");
        if (modal) {
            modal.hidden = true;
            modal.querySelector("[data-account-cancel-form]")?.reset();
            const error = modal.querySelector("[data-account-cancel-error]");
            if (error) {
                error.hidden = true;
            }
        }
        document.body.classList.remove("modal-open");
    }

    document.addEventListener("click", function (event) {
        const editButton = event.target.closest("[data-profile-edit]");
        if (editButton) {
            stopInteraction(event);
            const modal = currentModal();
            if (modal) {
                modal.hidden = false;
                document.body.classList.add("modal-open");
                modal.querySelector('[name="nickname"]')?.focus();
            }
            return;
        }
        if (event.target.closest("[data-profile-modal-close]")) {
            stopInteraction(event);
            closeModal();
            return;
        }
        if (event.target.closest("[data-account-cancel-open]")) {
            stopInteraction(event);
            const modal = document.querySelector("[data-account-cancel-modal]");
            if (modal) {
                modal.hidden = false;
                document.body.classList.add("modal-open");
                modal.querySelector('[name="password"]')?.focus();
            }
            return;
        }
        if (event.target.closest("[data-account-cancel-close]")) {
            stopInteraction(event);
            closeAccountCancelModal();
        }
    });

    document.addEventListener("submit", async function (event) {
        const cancelForm = event.target.closest("[data-account-cancel-form]");
        if (cancelForm) {
            stopInteraction(event);
            const submit = cancelForm.querySelector('[type="submit"]');
            const errorBox = cancelForm.querySelector(
                "[data-account-cancel-error]"
            );
            const values = Object.fromEntries(new FormData(cancelForm).entries());
            if (values.confirm !== "true") {
                errorBox.textContent = "请勾选注销确认";
                errorBox.hidden = false;
                return;
            }
            setBusy(submit, true);
            errorBox.hidden = true;
            try {
                const result = await postForm("/account/cancel", values);
                if (!result) {
                    return;
                }
                window.alert(result.message);
                window.location.replace(contextPath + "/login");
            } catch (error) {
                errorBox.textContent = error.message;
                errorBox.hidden = false;
            } finally {
                setBusy(submit, false);
            }
            return;
        }

        const form = event.target.closest("[data-profile-form]");
        if (!form) {
            return;
        }
        stopInteraction(event);
        const submit = form.querySelector('[type="submit"]');
        const errorBox = form.querySelector("[data-profile-form-error]");
        setBusy(submit, true);
        errorBox.hidden = true;
        try {
            const result = await postForm(
                "/profile/update",
                Object.fromEntries(new FormData(form).entries())
            );
            if (!result) {
                return;
            }
            document.querySelector(".user-name").textContent = result.nickname;
            updateCurrentUserAvatars(result.avatar, result.nickname);
            const headerAvatar = document.querySelector(".profile-avatar img");
            if (headerAvatar && result.avatar) {
                headerAvatar.src = contextPath
                    + (result.avatar.startsWith("/") ? "" : "/")
                    + result.avatar;
            }
            closeModal();
            showToast(result.message);
            window.CampusHubRouter?.reload();
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.hidden = false;
        } finally {
            setBusy(submit, false);
        }
    });

    document.addEventListener("click", async function (event) {
        const button = event.target.closest("[data-profile-unfavorite]");
        if (!button) {
            return;
        }
        stopInteraction(event);
        const targetType = button.dataset.targetType;
        const targetId = button.dataset.targetId;
        const path = targetType === "post"
            ? "/post/favorite"
            : "/goods/favorite";
        const values = targetType === "post"
            ? {postId: targetId}
            : {goodsId: targetId};
        setBusy(button, true);
        try {
            const result = await postForm(path, values);
            if (!result) {
                return;
            }
            button.closest("[data-profile-favorite-item]")?.remove();
            showToast("已取消收藏");
            window.CampusHubRouter?.reload();
        } catch (error) {
            showToast(error.message, true);
            setBusy(button, false);
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeModal();
            closeAccountCancelModal();
        }
    });
});
