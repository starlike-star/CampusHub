document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const authenticated =
        document.body.dataset.reportAuthenticated === "true";
    let targetType = "";
    let targetId = "";
    let toastTimer;

    const modal = document.createElement("div");
    modal.className = "report-modal";
    modal.hidden = true;
    modal.innerHTML = `
        <div class="report-modal-backdrop" data-report-close></div>
        <section class="report-dialog" role="dialog" aria-modal="true"
                 aria-labelledby="reportDialogTitle">
            <header>
                <div>
                    <span>CONTENT REPORT</span>
                    <h2 id="reportDialogTitle">举报内容</h2>
                </div>
                <button type="button" class="report-close"
                        data-report-close aria-label="关闭">×</button>
            </header>
            <form data-report-form>
                <label for="reportReason">举报原因</label>
                <div class="report-reason-options">
                    <button type="button" data-report-reason="垃圾广告">垃圾广告</button>
                    <button type="button" data-report-reason="不实信息">不实信息</button>
                    <button type="button" data-report-reason="辱骂攻击">辱骂攻击</button>
                    <button type="button" data-report-reason="违规交易">违规交易</button>
                    <button type="button" data-report-reason="涉嫌诈骗">涉嫌诈骗</button>
                </div>
                <textarea id="reportReason" name="reason" rows="5"
                          minlength="5" maxlength="255"
                          placeholder="请具体说明问题，至少 5 个字符"
                          required></textarea>
                <div class="report-form-meta">
                    <span data-report-error hidden></span>
                    <span><strong data-report-count>0</strong> / 255</span>
                </div>
                <footer>
                    <button type="button" data-report-close>取消</button>
                    <button type="submit" class="report-submit">提交举报</button>
                </footer>
            </form>
        </section>
    `;
    document.body.appendChild(modal);

    const toast = document.createElement("div");
    toast.className = "report-toast";
    toast.setAttribute("role", "status");
    document.body.appendChild(toast);

    const form = modal.querySelector("[data-report-form]");
    const textarea = form.elements.reason;
    const error = modal.querySelector("[data-report-error]");
    const count = modal.querySelector("[data-report-count]");
    const submit = modal.querySelector(".report-submit");

    function showToast(message, isError) {
        toast.textContent = message;
        toast.classList.toggle("error", Boolean(isError));
        toast.classList.add("show");
        window.clearTimeout(toastTimer);
        toastTimer = window.setTimeout(function () {
            toast.classList.remove("show");
        }, 2800);
    }

    function closeModal() {
        modal.hidden = true;
        document.body.classList.remove("report-modal-open");
        form.reset();
        count.textContent = "0";
        error.hidden = true;
    }

    function openModal(type, id) {
        targetType = type;
        targetId = id;
        modal.hidden = false;
        document.body.classList.add("report-modal-open");
        textarea.focus();
    }

    document.addEventListener("click", function (event) {
        const button = event.target.closest(".report-btn");
        if (!button) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        if (!authenticated) {
            showToast("请先登录后再举报", true);
            return;
        }
        openModal(button.dataset.targetType, button.dataset.targetId);
    });

    modal.addEventListener("click", function (event) {
        const reason = event.target.closest("[data-report-reason]");
        if (reason) {
            const value = reason.dataset.reportReason;
            textarea.value = textarea.value.trim()
                ? textarea.value.trim() + "；" + value
                : value + "：";
            textarea.dispatchEvent(new Event("input"));
            textarea.focus();
            return;
        }
        if (event.target.closest("[data-report-close]")) {
            closeModal();
        }
    });

    textarea.addEventListener("input", function () {
        count.textContent = String(textarea.value.length);
        error.hidden = true;
    });

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        const reason = textarea.value.trim();
        if (reason.length < 5) {
            error.textContent = "举报原因至少需要 5 个字符";
            error.hidden = false;
            textarea.focus();
            return;
        }

        submit.disabled = true;
        error.hidden = true;
        try {
            const response = await fetch(contextPath + "/report/create", {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: new URLSearchParams({
                    targetId: targetId,
                    targetType: targetType,
                    reason: reason
                }).toString()
            });
            const result = await response.json();
            if (!response.ok || !result.success) {
                throw new Error(result.message || "举报提交失败");
            }
            closeModal();
            showToast(result.message);
        } catch (failure) {
            error.textContent = failure.message || "举报提交失败";
            error.hidden = false;
        } finally {
            submit.disabled = false;
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && !modal.hidden) {
            closeModal();
        }
    });
});
