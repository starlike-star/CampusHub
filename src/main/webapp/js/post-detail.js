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

    const favoriteButton = document.querySelector(".detail-favorite-btn");
    if (favoriteButton) {
        favoriteButton.addEventListener("click", async function (event) {
            event.preventDefault();
            event.stopPropagation();
            favoriteButton.disabled = true;
            try {
                const result = await postForm("/post/favorite", {
                    postId: favoriteButton.dataset.postId
                });
                if (!result) {
                    return;
                }
                favoriteButton.classList.toggle("saved", result.favorited);
                favoriteButton.setAttribute(
                    "aria-pressed",
                    String(result.favorited)
                );
                favoriteButton.querySelector(
                    ".detail-favorite-label"
                ).textContent = result.favorited ? "取消收藏" : "收藏";
                favoriteButton.querySelector(
                    ".detail-favorite-count"
                ).textContent = result.favoriteCount;
            } catch (error) {
                window.alert(error.message);
            } finally {
                favoriteButton.disabled = false;
            }
        });
    }

    document.addEventListener("click", async function (event) {
        const button = event.target.closest("[data-comment-like]");
        if (!button) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        button.disabled = true;
        try {
            const result = await postForm("/comment/like", {
                commentId: button.dataset.commentId
            });
            if (!result) {
                return;
            }
            button.classList.toggle("active", result.liked);
            button.setAttribute("aria-pressed", String(result.liked));
            button.querySelector("strong").textContent = result.likeCount;
        } catch (error) {
            window.alert(error.message);
        } finally {
            button.disabled = false;
        }
    });

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
