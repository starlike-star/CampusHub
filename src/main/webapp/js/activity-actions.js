document.addEventListener("DOMContentLoaded", function () {
    if (window.activityActionsInitialized) {
        return;
    }
    window.activityActionsInitialized = true;
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";

    async function post(path, data) {
        const response = await fetch(contextPath + path, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: new URLSearchParams(data).toString()
        });
        const result = await response.json();
        if (result.needLogin) {
            window.location.href = contextPath + "/login";
            return null;
        }
        if (!response.ok || !result.success) {
            throw new Error(result.message || "操作失败");
        }
        return result;
    }

    function reloadCurrentView() {
        if (window.CampusHubRouter) {
            window.CampusHubRouter.reload();
        } else {
            window.location.reload();
        }
    }

    function openModal(data) {
        const modal = document.querySelector("[data-activity-modal]");
        const form = modal?.querySelector("[data-activity-form]");
        if (!modal || !form) {
            return;
        }
        form.reset();
        form.elements.id.value = "";
        if (data) {
            Object.entries(data).forEach(function ([name, value]) {
                if (form.elements[name]) {
                    form.elements[name].value = value || "";
                }
            });
        }
        window.CampusHubImageUpload?.sync(form);
        modal.hidden = false;
    }

    function closeModal() {
        const modal = document.querySelector("[data-activity-modal]");
        if (modal) {
            modal.hidden = true;
        }
    }

    function updateMemberDisplay(button, currentMembers) {
        const container = button.closest("[data-activity-card]") || document;
        const display = container.querySelector("[data-activity-members]");
        if (!display) {
            return;
        }
        const suffix = display.textContent.includes("不限人数")
            ? "不限人数"
            : display.textContent.split("/")[1]?.trim() || "0";
        display.textContent = currentMembers + " / " + suffix;
    }

    document.addEventListener("submit", async function (event) {
        const filter = event.target.closest("[data-activity-filter]");
        if (filter) {
            event.preventDefault();
            const values = new FormData(filter);
            const params = new URLSearchParams();
            const keyword = String(values.get("keyword") || "").trim();
            const status = String(values.get("status") || "all");
            const sort = String(values.get("sort") || "latest");
            if (keyword) {
                params.set("keyword", keyword);
            }
            if (status !== "all") {
                params.set("status", status);
            }
            if (sort !== "latest") {
                params.set("sort", sort);
            }
            window.CampusHubRouter?.navigate(
                "#activity" + (params.toString() ? "?" + params : "")
            );
            return;
        }

        const form = event.target.closest("[data-activity-form]");
        if (!form) {
            return;
        }
        event.preventDefault();
        const submit = form.querySelector('[type="submit"]');
        const error = form.querySelector("[data-activity-error]");
        submit.disabled = true;
        error.hidden = true;
        try {
            const data = Object.fromEntries(new FormData(form));
            await post(data.id ? "/activity/update" : "/activity/create", data);
            closeModal();
            form.reset();
            reloadCurrentView();
        } catch (failure) {
            error.textContent = failure.message;
            error.hidden = false;
        } finally {
            submit.disabled = false;
        }
    });

    document.addEventListener("change", function (event) {
        const select = event.target.closest("[data-activity-filter] select");
        if (select) {
            select.form.requestSubmit();
        }
    });

    document.addEventListener("click", async function (event) {
        if (event.target.closest("[data-activity-publish]")) {
            event.preventDefault();
            const authenticated =
                document.querySelector("[data-activity-auth]")?.dataset
                    .activityAuth;
            if (authenticated === "false") {
                window.location.href = contextPath + "/login";
                return;
            }
            openModal(null);
            return;
        }
        if (event.target.closest("[data-activity-close]")) {
            event.preventDefault();
            closeModal();
            return;
        }

        const edit = event.target.closest("[data-activity-edit]");
        if (edit) {
            event.preventDefault();
            openModal({
                id: edit.dataset.id,
                title: edit.dataset.title,
                content: edit.dataset.content,
                coverImage: edit.dataset.coverImage,
                location: edit.dataset.location,
                startTime: edit.dataset.startTime,
                endTime: edit.dataset.endTime,
                deadline: edit.dataset.deadline,
                maxMembers: edit.dataset.maxMembers
            });
            return;
        }

        const status = event.target.closest("[data-activity-status]");
        if (status) {
            event.preventDefault();
            status.disabled = true;
            try {
                await post("/activity/status", {
                    id: status.dataset.id,
                    status: status.dataset.status
                });
                reloadCurrentView();
            } catch (failure) {
                status.disabled = false;
                window.alert(failure.message);
            }
            return;
        }

        const register = event.target.closest("[data-activity-register]");
        const cancel = event.target.closest("[data-activity-cancel]");
        const action = register || cancel;
        if (!action) {
            return;
        }
        event.preventDefault();
        action.disabled = true;
        try {
            const result = await post(
                register ? "/activity/register" : "/activity/cancel",
                {activityId: action.dataset.id}
            );
            if (!result) {
                return;
            }
            updateMemberDisplay(action, result.currentMembers);
            action.textContent = result.registered ? "取消报名" : "立即报名";
            action.removeAttribute(
                result.registered
                    ? "data-activity-register"
                    : "data-activity-cancel"
            );
            action.setAttribute(
                result.registered
                    ? "data-activity-cancel"
                    : "data-activity-register",
                ""
            );
            action.disabled = false;
        } catch (failure) {
            action.disabled = false;
            window.alert(failure.message);
        }
    });
});
