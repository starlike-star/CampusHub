document.addEventListener("DOMContentLoaded", function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const accountButton = document.getElementById("accountMenuButton");
    const accountMenu = document.getElementById("accountMenu");
    const checkinButton = document.getElementById("checkinButton");
    const toast = document.getElementById("toast");
    const editModal = document.getElementById("postEditModal");
    const editForm = document.getElementById("postEditForm");
    const editError = document.getElementById("editFormError");
    let toastTimer;

    function stopInteraction(event) {
        event.preventDefault();
        event.stopPropagation();
    }

    function showToast(message, isError) {
        if (!toast) {
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

    async function postForm(url, data) {
        const body = new URLSearchParams();
        Object.entries(data).forEach(function ([key, value]) {
            body.append(key, value == null ? "" : String(value));
        });
        const response = await fetch(contextPath + url, {
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

    function getCard(element) {
        return element.closest(".post-card");
    }

    function setButtonBusy(button, busy) {
        button.disabled = busy;
        button.classList.toggle("is-loading", busy);
    }

    if (accountButton && accountMenu) {
        accountButton.addEventListener("click", function (event) {
            stopInteraction(event);
            const isOpen = accountMenu.classList.toggle("open");
            accountButton.setAttribute("aria-expanded", String(isOpen));
        });
        document.addEventListener("click", function (event) {
            if (!event.target.closest(".account-wrap")) {
                accountMenu.classList.remove("open");
                accountButton.setAttribute("aria-expanded", "false");
            }
        });
    }

    if (checkinButton) {
        checkinButton.addEventListener("click", async function (event) {
            stopInteraction(event);
            if (checkinButton.classList.contains("checked")) {
                return;
            }
            setButtonBusy(checkinButton, true);
            try {
                const response = await fetch(contextPath + "/checkin/do", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                    }
                });
                const result = await response.json();
                if (result.needLogin) {
                    window.location.assign(contextPath + "/login");
                    return;
                }
                if (result.checkedIn) {
                    checkinButton.classList.add("checked");
                    checkinButton.textContent = "已签到";
                    checkinButton.disabled = true;
                    document.getElementById("checkinStatus").textContent = "今日已签到";
                }
                if (!response.ok || !result.success) {
                    showToast(result.message || "签到失败", true);
                    return;
                }
                document.getElementById("checkinPoints").textContent =
                    "+" + result.points;
                document.getElementById("streakDays").textContent =
                    result.continuousDays + " 天";
                document.getElementById("checkinProgress").style.width =
                    Math.min(result.continuousDays * 100 / 7, 100) + "%";
                showToast(result.message + "，积分 +" + result.points);
            } catch (error) {
                showToast("签到失败，请稍后重试", true);
            } finally {
                const checked = checkinButton.classList.contains("checked");
                setButtonBusy(checkinButton, false);
                checkinButton.disabled = checked;
            }
        });
    }

    document.addEventListener("click", function (event) {
        const area = event.target.closest(".post-click-area");
        if (!area) {
            return;
        }
        event.preventDefault();
        window.location.assign(area.dataset.detailUrl);
    });

    document.addEventListener("keydown", function (event) {
        const area = event.target.closest(".post-click-area");
        if (area && (event.key === "Enter" || event.key === " ")) {
            event.preventDefault();
            window.location.assign(area.dataset.detailUrl);
        }
    });

    document.addEventListener("click", async function (event) {
        const action = event.target.closest(".post-action");
        if (!action) {
            return;
        }
        stopInteraction(event);
        const card = getCard(action);
        const postId = action.dataset.postId || card?.dataset.postId;
        if (!card || !postId) {
            return;
        }

        if (action.classList.contains("like-btn")) {
            setButtonBusy(action, true);
            try {
                const result = await postForm("/post/like", {postId: postId});
                if (!result) {
                    return;
                }
                action.classList.toggle("liked", result.liked);
                action.setAttribute("aria-pressed", String(result.liked));
                action.querySelector(".like-count").textContent = result.likeCount;
            } catch (error) {
                showToast(error.message, true);
            } finally {
                setButtonBusy(action, false);
            }
            return;
        }

        if (action.classList.contains("favorite-btn")) {
            setButtonBusy(action, true);
            try {
                const result = await postForm("/post/favorite", {postId: postId});
                if (!result) {
                    return;
                }
                action.classList.toggle("saved", result.favorited);
                action.setAttribute("aria-pressed", String(result.favorited));
                action.querySelector(".favorite-count-value").textContent =
                    result.favoriteCount;
            } catch (error) {
                showToast(error.message, true);
            } finally {
                setButtonBusy(action, false);
            }
            return;
        }

        if (action.classList.contains("comment-toggle-btn")) {
            const panel = card.querySelector(".quick-comment-panel");
            panel.hidden = !panel.hidden;
            if (!panel.hidden) {
                const textarea = panel.querySelector("textarea");
                window.resizeTextarea?.(textarea);
                textarea.focus();
            }
            return;
        }

        if (action.classList.contains("edit-post-btn")) {
            openEditModal(card, action);
            return;
        }

        if (action.classList.contains("delete-post-btn")) {
            if (!window.confirm("确认删除这条帖子吗？")) {
                return;
            }
            setButtonBusy(action, true);
            try {
                const result = await postForm("/post/delete", {postId: postId});
                if (!result) {
                    return;
                }
                card.classList.add("is-removing");
                window.setTimeout(function () {
                    card.remove();
                }, 180);
                showToast("帖子已删除");
            } catch (error) {
                setButtonBusy(action, false);
                showToast(error.message, true);
            }
        }
    });

    document.addEventListener("click", function (event) {
        const cancelButton = event.target.closest(".cancel-comment-btn");
        if (!cancelButton) {
            return;
        }
        stopInteraction(event);
        cancelButton.closest(".quick-comment-panel").hidden = true;
    });

    document.addEventListener("submit", async function (event) {
        const form = event.target.closest(".quick-comment-form");
        if (!form) {
            return;
        }
        stopInteraction(event);
        const card = getCard(form);
        const textarea = form.querySelector("textarea");
        const submitButton = form.querySelector(".quick-comment-submit");
        const content = textarea.value.trim();
        if (!content) {
            showToast("请输入评论内容", true);
            textarea.focus();
            return;
        }
        setButtonBusy(submitButton, true);
        try {
            const result = await postForm("/post/comment", {
                postId: card.dataset.postId,
                content: content
            });
            if (!result) {
                return;
            }
            textarea.value = "";
            window.resizeTextarea?.(textarea);
            card.querySelector(".comment-count").textContent = result.commentCount;
            appendQuickComment(card, result.comment);
            showToast("评论成功");
        } catch (error) {
            showToast(error.message, true);
        } finally {
            setButtonBusy(submitButton, false);
        }
    });

    function appendQuickComment(card, comment) {
        const item = document.createElement("div");
        item.className = "quick-comment-item";

        const avatar = document.createElement("span");
        avatar.className = "quick-comment-avatar";
        if (comment.avatar) {
            const image = document.createElement("img");
            image.src = contextPath
                + (comment.avatar.startsWith("/") ? "" : "/")
                + comment.avatar;
            image.alt = comment.nickname;
            avatar.appendChild(image);
        } else {
            avatar.textContent = (comment.nickname || "U").slice(0, 1);
        }

        const content = document.createElement("div");
        const header = document.createElement("strong");
        header.textContent = comment.nickname + " · " + comment.createdAt;
        const text = document.createElement("p");
        text.textContent = comment.content;
        content.append(header, text);
        item.append(avatar, content);
        card.querySelector(".quick-comment-list").prepend(item);
    }

    function openEditModal(card, button) {
        if (!editModal || !editForm) {
            return;
        }
        editForm.elements.postId.value = card.dataset.postId;
        editForm.elements.title.value =
            card.querySelector(".post-title").textContent.trim();
        editForm.elements.content.value =
            card.querySelector(".post-raw-content").value;
        editForm.elements.topic.value =
            card.querySelector(".post-topic").textContent.trim().replace(/^#/, "");
        editForm.elements.categoryId.value = button.dataset.categoryId;
        editForm.elements.images.value = card.dataset.images || "";
        editForm.dataset.cardPostId = card.dataset.postId;
        editError.hidden = true;
        editModal.hidden = false;
        document.body.classList.add("modal-open");
        window.CampusHubImageUpload?.sync(editForm);
        window.resizeTextarea?.(editForm.elements.content);
        editForm.elements.title.focus();
    }

    function closeEditModal() {
        if (!editModal) {
            return;
        }
        editModal.hidden = true;
        document.body.classList.remove("modal-open");
    }

    document.querySelectorAll("[data-close-edit-modal]").forEach(function (button) {
        button.addEventListener("click", function (event) {
            stopInteraction(event);
            closeEditModal();
        });
    });

    if (editForm) {
        editForm.addEventListener("submit", async function (event) {
            stopInteraction(event);
            const saveButton = editForm.querySelector(".save-post-btn");
            setButtonBusy(saveButton, true);
            editError.hidden = true;
            try {
                const result = await postForm("/post/update", {
                    postId: editForm.elements.postId.value,
                    title: editForm.elements.title.value,
                    content: editForm.elements.content.value,
                    topic: editForm.elements.topic.value,
                    categoryId: editForm.elements.categoryId.value,
                    images: editForm.elements.images.value
                });
                if (!result) {
                    return;
                }
                const card = document.querySelector(
                    '.post-card[data-post-id="' + result.post.id + '"]'
                );
                card.querySelector(".post-title").textContent = result.post.title;
                card.querySelector(".post-summary").textContent = result.post.summary;
                card.querySelector(".post-raw-content").value = result.post.content;
                card.querySelector(".category-badge").textContent =
                    result.post.categoryName;
                const topic = card.querySelector(".post-topic");
                topic.textContent = result.post.topic;
                topic.classList.toggle("is-empty", !result.post.topic);
                card.querySelector(".edit-post-btn").dataset.categoryId =
                    result.post.categoryId;
                card.dataset.images = result.post.images;
                closeEditModal();
                showToast("帖子已更新");
            } catch (error) {
                editError.textContent = error.message;
                editError.hidden = false;
            } finally {
                setButtonBusy(saveButton, false);
            }
        });
    }

    document.addEventListener("keydown", function (event) {
        if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "k") {
            const searchInput = document.querySelector(".global-search input");
            if (searchInput) {
                event.preventDefault();
                searchInput.focus();
            }
        }
        if (event.key === "Escape") {
            closeEditModal();
            if (accountMenu) {
                accountMenu.classList.remove("open");
                accountButton.setAttribute("aria-expanded", "false");
            }
        }
    });
});
