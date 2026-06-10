document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";

    function reloadFragment() {
        window.CampusHubRouter?.reload();
    }

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

    function openPublishModal(data) {
        const modal = document.querySelector("[data-lostfound-modal]");
        const form = modal?.querySelector("[data-lostfound-form]");
        if (!modal || !form) {
            return;
        }
        form.reset();
        form.elements.id.value = "";
        modal.querySelector("[data-lostfound-modal-title]").textContent =
            data ? "编辑失物 / 招领" : "发布失物 / 招领";
        if (data) {
            Object.entries(data).forEach(function ([name, value]) {
                if (form.elements[name]) {
                    form.elements[name].value = value || "";
                }
            });
        }
        modal.hidden = false;
    }

    function close(selector) {
        const modal = document.querySelector(selector);
        if (modal) {
            modal.hidden = true;
        }
    }

    document.addEventListener("submit", async function (event) {
        const filter = event.target.closest("[data-lostfound-filter]");
        if (filter) {
            event.preventDefault();
            const params = new URLSearchParams();
            new FormData(filter).forEach(function (value, key) {
                const normalized = String(value).trim();
                if (normalized && normalized !== "all"
                        && !(key === "sort" && normalized === "latest")) {
                    params.set(key, normalized);
                }
            });
            window.CampusHubRouter?.navigate(
                "#lostfound" + (params.toString() ? "?" + params : "")
            );
            return;
        }

        const form = event.target.closest("[data-lostfound-form]");
        if (form) {
            event.preventDefault();
            const submit = form.querySelector('[type="submit"]');
            const error = form.querySelector("[data-lostfound-error]");
            submit.disabled = true;
            error.hidden = true;
            try {
                const data = Object.fromEntries(new FormData(form));
                const path = data.id
                    ? "/lostfound/update"
                    : "/lostfound/create";
                await post(path, data);
                close("[data-lostfound-modal]");
                form.reset();
                reloadFragment();
            } catch (failure) {
                error.textContent = failure.message;
                error.hidden = false;
            } finally {
                submit.disabled = false;
            }
            return;
        }

        const claimForm = event.target.closest("[data-claim-form]");
        if (!claimForm) {
            return;
        }
        event.preventDefault();
        const submit = claimForm.querySelector('[type="submit"]');
        const error = claimForm.querySelector("[data-claim-error]");
        submit.disabled = true;
        error.hidden = true;
        try {
            await post(
                "/claim/create",
                Object.fromEntries(new FormData(claimForm))
            );
            close("[data-claim-modal]");
            claimForm.reset();
            if (window.CampusHubRouter) {
                reloadFragment();
            } else {
                window.location.reload();
            }
        } catch (failure) {
            error.textContent = failure.message;
            error.hidden = false;
        } finally {
            submit.disabled = false;
        }
    });

    document.addEventListener("change", async function (event) {
        const filterField = event.target.closest(
            "[data-lostfound-filter] select"
        );
        if (filterField) {
            filterField.form.requestSubmit();
            return;
        }
        const status = event.target.closest("[data-lostfound-status]");
        if (!status) {
            return;
        }
        status.disabled = true;
        try {
            await post("/lostfound/status", {
                id: status.dataset.id,
                status: status.value
            });
            reloadFragment();
        } catch (failure) {
            window.alert(failure.message);
            reloadFragment();
        }
    });

    document.addEventListener("click", async function (event) {
        const statusButton = event.target.closest(
            "[data-lostfound-status-button]"
        );
        if (statusButton) {
            event.preventDefault();
            statusButton.disabled = true;
            try {
                await post("/lostfound/status", {
                    id: statusButton.dataset.id,
                    status: statusButton.dataset.status
                });
                if (window.CampusHubRouter) {
                    reloadFragment();
                } else {
                    window.location.reload();
                }
            } catch (failure) {
                statusButton.disabled = false;
                window.alert(failure.message);
            }
            return;
        }
        const profileStatusButton = event.target.closest(
            "button[data-lostfound-status][data-status]"
        );
        if (profileStatusButton) {
            event.preventDefault();
            profileStatusButton.disabled = true;
            try {
                await post("/lostfound/status", {
                    id: profileStatusButton.dataset.id,
                    status: profileStatusButton.dataset.status
                });
                reloadFragment();
            } catch (failure) {
                profileStatusButton.disabled = false;
                window.alert(failure.message);
            }
            return;
        }
        const claimHandle = event.target.closest("[data-claim-handle]");
        if (claimHandle) {
            event.preventDefault();
            claimHandle.disabled = true;
            try {
                await post("/claim/handle", {
                    claimId: claimHandle.dataset.claimId,
                    action: claimHandle.dataset.action
                });
                window.location.reload();
            } catch (failure) {
                claimHandle.disabled = false;
                window.alert(failure.message);
            }
            return;
        }
        if (event.target.closest("[data-lostfound-publish]")) {
            event.preventDefault();
            openPublishModal(null);
            return;
        }
        const edit = event.target.closest("[data-lostfound-edit]");
        if (edit) {
            event.preventDefault();
            openPublishModal({
                id: edit.dataset.id,
                type: edit.dataset.type,
                itemName: edit.dataset.itemName,
                title: edit.dataset.title,
                categoryId: edit.dataset.categoryId,
                description: edit.dataset.description,
                place: edit.dataset.place,
                eventTime: edit.dataset.eventTime,
                images: edit.dataset.images,
                contact: edit.dataset.contact
            });
            return;
        }
        if (event.target.closest("[data-lostfound-close]")) {
            event.preventDefault();
            close("[data-lostfound-modal]");
            return;
        }
        const claimOpen = event.target.closest("[data-claim-open]");
        if (claimOpen) {
            event.preventDefault();
            const modal = document.querySelector("[data-claim-modal]");
            const form = modal?.querySelector("[data-claim-form]");
            if (!modal || !form) {
                return;
            }
            form.reset();
            form.elements.lostFoundId.value = claimOpen.dataset.id;
            modal.querySelector("[data-claim-title]").textContent =
                "申请认领《" + claimOpen.dataset.title + "》";
            modal.hidden = false;
            return;
        }
        if (event.target.closest("[data-claim-close]")) {
            event.preventDefault();
            close("[data-claim-modal]");
        }
    });
});
