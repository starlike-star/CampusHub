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

    function setButtonBusy(button, busy) {
        button.disabled = busy;
        button.classList.toggle("is-loading", busy);
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

    function currentMarketParams() {
        const route = window.CampusHubRouter?.parseHash();
        return route?.page === "market"
            ? new URLSearchParams(route.params)
            : new URLSearchParams();
    }

    function navigateMarket(params) {
        const query = params.toString();
        const hash = "#market" + (query ? "?" + query : "");
        if (window.CampusHubRouter) {
            window.CampusHubRouter.navigate(hash);
        } else {
            window.location.hash = hash;
        }
    }

    function refreshGoodsView(removed) {
        if (window.CampusHubRouter) {
            window.CampusHubRouter.reload();
            return;
        }
        if (removed) {
            window.location.assign(contextPath + "/home#my-goods");
            return;
        }
        window.location.reload();
    }

    function getModal() {
        return document.querySelector("[data-goods-modal]");
    }

    function updateTradePlaceHint(form) {
        const hint = form.querySelector("[data-trade-place-hint]");
        if (!hint) {
            return;
        }
        const method = form.elements.tradeMethod.value;
        hint.hidden = method === "online";
    }

    function openGoodsModal(record) {
        const modal = getModal();
        const form = modal?.querySelector("[data-goods-form]");
        if (!modal || !form) {
            return;
        }
        form.reset();
        form.elements.goodsId.value = "";
        modal.querySelector("#goodsModalTitle").textContent = "发布商品";
        form.querySelector('[type="submit"]').textContent = "确认发布";
        form.querySelector("[data-goods-form-error]").hidden = true;

        form.elements.tradeMethod.value = "offline";

        if (record) {
            form.elements.goodsId.value = record.dataset.goodsId;
            form.elements.title.value = record.dataset.title || "";
            form.elements.description.value =
                record.querySelector(".goods-raw-description").value;
            form.elements.price.value = record.dataset.price || "";
            form.elements.categoryId.value = record.dataset.categoryId || "";
            form.elements.conditionLevel.value =
                record.dataset.conditionLevel || "";
            form.elements.images.value = record.dataset.images || "";
            form.elements.tradePlace.value = record.dataset.tradePlace || "";
            form.elements.tradeMethod.value =
                record.dataset.tradeMethod || "offline";
            form.elements.contact.value = record.dataset.contact || "";
            modal.querySelector("#goodsModalTitle").textContent = "编辑商品";
            form.querySelector('[type="submit"]').textContent = "保存修改";
        }

        updateTradePlaceHint(form);
        modal.hidden = false;
        document.body.classList.add("modal-open");
        window.resizeTextarea?.(form.elements.description);
        form.elements.title.focus();
    }

    function closeGoodsModal() {
        const modal = getModal();
        if (modal) {
            modal.hidden = true;
        }
        document.body.classList.remove("modal-open");
    }

    document.addEventListener("click", function (event) {
        const filter = event.target.closest("[data-market-filter]");
        if (!filter) {
            return;
        }
        stopInteraction(event);
        const params = currentMarketParams();
        const name = filter.dataset.marketFilter;
        const value = filter.dataset.filterValue;
        if (value) {
            params.set(name, value);
        } else {
            params.delete(name);
        }
        navigateMarket(params);
    });

    document.addEventListener("change", async function (event) {
        const sort = event.target.closest("[data-market-sort]");
        if (sort) {
            const params = currentMarketParams();
            if (sort.value === "latest") {
                params.delete("sort");
            } else {
                params.set("sort", sort.value);
            }
            navigateMarket(params);
            return;
        }

        const statusSelect = event.target.closest(
            '[data-goods-action="status"]'
        );
        if (!statusSelect) {
            return;
        }
        event.stopPropagation();
        const record = statusSelect.closest("[data-goods-record]");
        statusSelect.disabled = true;
        try {
            const result = await postForm("/goods/status", {
                goodsId: record.dataset.goodsId,
                status: statusSelect.value
            });
            if (result) {
                showToast(result.message);
                refreshGoodsView(statusSelect.value === "off_shelf");
            }
        } catch (error) {
            showToast(error.message, true);
            refreshGoodsView(false);
        } finally {
            statusSelect.disabled = false;
        }
    });

    document.addEventListener("submit", async function (event) {
        const searchForm = event.target.closest("[data-market-search]");
        if (searchForm) {
            event.preventDefault();
            const params = currentMarketParams();
            const keyword =
                new FormData(searchForm).get("keyword")?.toString().trim() || "";
            if (keyword) {
                params.set("keyword", keyword);
            } else {
                params.delete("keyword");
            }
            navigateMarket(params);
            return;
        }

        const goodsForm = event.target.closest("[data-goods-form]");
        if (!goodsForm) {
            return;
        }
        stopInteraction(event);
        const submit = goodsForm.querySelector('[type="submit"]');
        const errorBox = goodsForm.querySelector("[data-goods-form-error]");
        const values = Object.fromEntries(new FormData(goodsForm).entries());
        const editing = Boolean(values.goodsId);
        setButtonBusy(submit, true);
        errorBox.hidden = true;
        try {
            const result = await postForm(
                editing ? "/goods/update" : "/goods/create",
                values
            );
            if (!result) {
                return;
            }
            closeGoodsModal();
            showToast(result.message);
            refreshGoodsView(false);
        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.hidden = false;
        } finally {
            setButtonBusy(submit, false);
        }
    });

    document.addEventListener("click", async function (event) {
        const openButton = event.target.closest("[data-open-goods-modal]");
        if (openButton) {
            stopInteraction(event);
            if (openButton.dataset.authenticated !== "true") {
                showToast("请先登录");
                window.setTimeout(function () {
                    window.location.assign(contextPath + "/login");
                }, 500);
                return;
            }
            openGoodsModal(null);
            return;
        }

        if (event.target.closest("[data-close-goods-modal]")) {
            stopInteraction(event);
            closeGoodsModal();
            return;
        }

        const action = event.target.closest("[data-goods-action]");
        if (!action || action.matches("select")) {
            return;
        }
        stopInteraction(event);
        const record = action.closest("[data-goods-record], .goods-card");
        const goodsId =
            action.dataset.goodsId || record?.dataset.goodsId;
        if (!goodsId) {
            return;
        }

        if (action.dataset.goodsAction === "favorite") {
            setButtonBusy(action, true);
            try {
                const result = await postForm("/goods/favorite", {
                    goodsId: goodsId
                });
                if (!result) {
                    return;
                }
                action.classList.toggle("saved", result.favorited);
                action.setAttribute(
                    "aria-pressed",
                    String(result.favorited)
                );
                action.querySelector("span").textContent = result.favoriteCount;
                if (!result.favorited
                        && window.CampusHubRouter?.parseHash().page
                                === "favorites") {
                    refreshGoodsView(false);
                }
            } catch (error) {
                showToast(error.message, true);
            } finally {
                setButtonBusy(action, false);
            }
            return;
        }

        if (action.dataset.goodsAction === "edit") {
            openGoodsModal(record);
            return;
        }

        if (action.dataset.goodsAction === "delete") {
            if (!window.confirm("确认下架这件商品吗？")) {
                return;
            }
            setButtonBusy(action, true);
            try {
                const result = await postForm("/goods/delete", {
                    goodsId: goodsId
                });
                if (result) {
                    showToast("商品已下架");
                    refreshGoodsView(true);
                }
            } catch (error) {
                showToast(error.message, true);
                setButtonBusy(action, false);
            }
        }
    });

    document.addEventListener("change", function (event) {
        const tradeMethod = event.target.closest(
            '[data-goods-form] [name="tradeMethod"]'
        );
        if (tradeMethod) {
            updateTradePlaceHint(tradeMethod.form);
        }
    });

    document.addEventListener("click", function (event) {
        const wantButton = event.target.closest("[data-want-goods]");
        if (wantButton) {
            stopInteraction(event);
            window.alert(wantButton.dataset.wantMessage);
            return;
        }
        const contactButton = event.target.closest("[data-contact-seller]");
        if (contactButton) {
            stopInteraction(event);
            const contact = contactButton.dataset.contactSeller;
            window.alert(
                contact
                    ? "卖家联系方式：" + contact
                    : "卖家暂未填写联系方式。"
            );
            return;
        }
        const thumbnail = event.target.closest("[data-goods-thumbnail]");
        if (thumbnail) {
            stopInteraction(event);
            const mainImage = document.querySelector(".goods-main-image");
            if (mainImage) {
                mainImage.src = thumbnail.dataset.goodsThumbnail;
            }
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeGoodsModal();
        }
    });
});
