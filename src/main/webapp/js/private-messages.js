document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("[data-private-message-form]");
    if (!form) {
        return;
    }
    const textarea = form.querySelector("textarea[name='content']");
    const counter = form.querySelector("[data-private-message-count]");
    const error = form.querySelector("[data-private-message-error]");
    const submit = form.querySelector("button[type='submit']");
    const thread = document.querySelector("[data-private-thread-messages]");

    function updateCount() {
        counter.textContent = Array.from(textarea.value).length + " / 1000";
    }

    textarea.addEventListener("input", updateCount);
    updateCount();
    thread?.scrollTo({top: thread.scrollHeight});

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        error.hidden = true;
        submit.disabled = true;
        try {
            const response = await fetch(form.action, {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: new URLSearchParams(new FormData(form)).toString()
            });
            const result = await response.json();
            if (!response.ok || !result.success) {
                throw new Error(result.message || "发送失败");
            }
            window.location.reload();
        } catch (requestError) {
            error.textContent = requestError.message;
            error.hidden = false;
            submit.disabled = false;
        }
    });
});
