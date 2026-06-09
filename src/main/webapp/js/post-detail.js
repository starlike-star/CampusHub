document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";

    async function postForm(url, data) {
        const body = new URLSearchParams();
        Object.entries(data).forEach(function ([key, value]) {
            body.append(key, String(value));
        });
        const response = await fetch(contextPath + url, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: body.toString()
        });
        const result = await response.json();
        if (result.needLogin) {
            window.location.assign(contextPath + "/login");
            return null;
        }
        if (!response.ok || !result.success) {
            throw new Error(result.message || "操作失败");
        }
        return result;
    }

    const likeButton = document.querySelector(".detail-like-btn");
    if (likeButton) {
        likeButton.addEventListener("click", async function (event) {
            event.preventDefault();
            event.stopPropagation();
            likeButton.disabled = true;
            try {
                const result = await postForm("/post/like", {
                    postId: likeButton.dataset.postId
                });
                if (!result) {
                    return;
                }
                likeButton.classList.toggle("liked", result.liked);
                likeButton.setAttribute("aria-pressed", String(result.liked));
                likeButton.querySelector(".detail-like-label").textContent =
                    result.liked ? "取消点赞" : "点赞";
                likeButton.querySelector(".detail-like-count").textContent =
                    result.likeCount;
            } catch (error) {
                window.alert(error.message);
            } finally {
                likeButton.disabled = false;
            }
        });
    }

    const commentForm = document.querySelector(".detail-comment-form");
    if (commentForm) {
        commentForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            event.stopPropagation();
            const textarea = commentForm.querySelector("textarea");
            const submitButton = commentForm.querySelector("button");
            const content = textarea.value.trim();
            if (!content) {
                return;
            }
            submitButton.disabled = true;
            try {
                const result = await postForm("/post/comment", {
                    postId: commentForm.dataset.postId,
                    content: content
                });
                if (!result) {
                    return;
                }
                textarea.value = "";
                window.resizeTextarea?.(textarea);
                window.location.reload();
            } catch (error) {
                window.alert(error.message);
            } finally {
                submitButton.disabled = false;
            }
        });
    }
});
