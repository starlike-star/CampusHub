(function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const modal = document.querySelector("[data-trade-modal]");
    if (!modal) {
        return;
    }

    let pollTimer;
    let countdownTimer;
    let currentOrderNo;

    function field(name) {
        return modal.querySelector("[data-trade-" + name + "]");
    }

    function setStatus(message, type) {
        const status = field("status");
        status.textContent = message;
        status.className = "trade-status" + (type ? " " + type : "");
    }

    function stopTimers() {
        window.clearInterval(pollTimer);
        window.clearInterval(countdownTimer);
        pollTimer = null;
        countdownTimer = null;
    }

    function closeModal() {
        stopTimers();
        modal.hidden = true;
        document.body.classList.remove("modal-open");
    }

    async function readJson(response) {
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

    async function createOrder(goodsId) {
        const body = new URLSearchParams({goodsId: goodsId});
        return readJson(await fetch(contextPath + "/trade/order/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: body.toString()
        }));
    }

    async function pollStatus() {
        if (!currentOrderNo) {
            return;
        }
        try {
            const response = await fetch(
                contextPath + "/trade/order/status?orderNo="
                    + encodeURIComponent(currentOrderNo),
                {cache: "no-store"}
            );
            const result = await readJson(response);
            if (!result) {
                return;
            }
            if (result.status === "paid") {
                stopTimers();
                setStatus("支付成功", "success");
                field("countdown").textContent = "商品已更新为已售出";
                field("complete").hidden = false;
            } else if (result.status === "expired") {
                stopTimers();
                setStatus("订单已过期，请重新下单", "error");
                field("countdown").textContent = "";
            }
        } catch (error) {
            setStatus(error.message, "error");
        }
    }

    function startCountdown(expireAt) {
        function render() {
            const seconds = Math.max(
                0,
                Math.ceil((expireAt - Date.now()) / 1000)
            );
            const minutes = String(Math.floor(seconds / 60)).padStart(2, "0");
            const rest = String(seconds % 60).padStart(2, "0");
            field("countdown").textContent = "支付倒计时 " + minutes + ":" + rest;
            if (seconds <= 0) {
                window.clearInterval(countdownTimer);
                pollStatus();
            }
        }
        render();
        countdownTimer = window.setInterval(render, 1000);
    }

    function openModal(order) {
        stopTimers();
        currentOrderNo = order.orderNo;
        field("title").textContent = order.title;
        field("order-no").textContent = order.orderNo;
        field("amount").textContent = "¥" + Number(order.amount).toFixed(2);
        field("qrcode").src = order.qrcodeUrl + "&t=" + Date.now();
        field("warning").hidden = !order.localhostWarning;
        field("complete").hidden = true;
        setStatus("等待扫码支付");
        modal.hidden = false;
        document.body.classList.add("modal-open");
        startCountdown(Number(order.expireAt));
        pollTimer = window.setInterval(pollStatus, 2000);
    }

    document.addEventListener("click", async function (event) {
        const wantButton = event.target.closest("[data-want-goods]");
        if (wantButton) {
            event.preventDefault();
            event.stopImmediatePropagation();
            if (wantButton.dataset.tradeMethod === "offline") {
                window.alert("该商品仅支持线下交易，请通过私信联系卖家");
                return;
            }
            wantButton.disabled = true;
            try {
                const order = await createOrder(wantButton.dataset.goodsId);
                if (order) {
                    openModal(order);
                }
            } catch (error) {
                window.alert(error.message);
            } finally {
                wantButton.disabled = false;
            }
            return;
        }
        if (event.target.closest("[data-close-trade-modal]")) {
            event.preventDefault();
            closeModal();
        }
        if (event.target.closest("[data-trade-complete]")) {
            closeModal();
            window.location.reload();
        }
    }, true);

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && !modal.hidden) {
            closeModal();
        }
    });
})();
