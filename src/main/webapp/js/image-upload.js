// 封装图片选择、上传、预览、数量限制与失败提示交互。
(function () {
    const contextPath =
        document.querySelector('meta[name="context-path"]')?.content || "";
    const maxFileSize = 5 * 1024 * 1024;
    const allowedExtensions = new Set(["jpg", "jpeg", "png", "webp"]);
    const allowedMimeTypes = new Set([
        "image/jpeg", "image/png", "image/webp"
    ]);

    function assetUrl(path) {
        if (!path) {
            return "";
        }
        return contextPath + (path.startsWith("/") ? path : "/" + path);
    }

    function valuesOf(widget) {
        const target = widget.querySelector("[data-image-upload-value]");
        return String(target?.value || "")
            .split(",")
            .map(function (value) {
                return value.trim();
            })
            .filter(Boolean);
    }

    function render(widget) {
        const preview = widget.querySelector("[data-image-upload-preview]");
        if (!preview) {
            return;
        }
        const values = valuesOf(widget);
        preview.replaceChildren();
        preview.hidden = values.length === 0;
        values.forEach(function (value) {
            const image = document.createElement("img");
            image.src = assetUrl(value);
            image.alt = "已上传图片预览";
            preview.appendChild(image);
        });
    }

    function setState(widget, busy, message, isError) {
        const button = widget.querySelector("[data-image-upload-button]");
        const input = widget.querySelector("[data-image-upload-input]");
        const status = widget.querySelector("[data-image-upload-status]");
        if (button) {
            button.disabled = busy;
            button.classList.toggle("is-loading", busy);
            button.textContent = busy ? "上传中..." : "选择图片";
        }
        if (input) {
            input.disabled = busy;
        }
        if (status) {
            status.textContent = message || "";
            status.classList.toggle("error", Boolean(isError));
        }
    }

    function validate(file) {
        const extension = file.name.includes(".")
            ? file.name.split(".").pop().toLowerCase()
            : "";
        if (!allowedExtensions.has(extension)
                || !allowedMimeTypes.has(file.type.toLowerCase())) {
            throw new Error("只支持 jpg、jpeg、png、webp 图片");
        }
        if (file.size > maxFileSize) {
            throw new Error("单张图片不能超过 5MB");
        }
    }

    async function upload(file, type) {
        const body = new FormData();
        body.append("file", file);
        body.append("type", type);
        const response = await fetch(contextPath + "/upload/image", {
            method: "POST",
            body: body
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
            throw new Error(result.message || "图片上传失败");
        }
        return result.url;
    }

    async function handleSelection(input) {
        const widget = input.closest("[data-image-upload]");
        const files = Array.from(input.files || []);
        if (!widget || files.length === 0) {
            return;
        }
        try {
            files.forEach(validate);
            setState(widget, true, "正在上传...");
            const urls = [];
            for (const file of files) {
                const url = await upload(file, widget.dataset.imageUpload);
                if (!url) {
                    return;
                }
                urls.push(url);
            }
            const target = widget.querySelector("[data-image-upload-value]");
            target.value = urls.join(",");
            render(widget);
            setState(widget, false, "上传成功，可重新选择", false);
            target.dispatchEvent(new Event("change", {bubbles: true}));
        } catch (error) {
            setState(widget, false, error.message, true);
        } finally {
            input.value = "";
        }
    }

    document.addEventListener("click", function (event) {
        const button = event.target.closest("[data-image-upload-button]");
        if (!button) {
            return;
        }
        event.preventDefault();
        button.closest("[data-image-upload]")
            ?.querySelector("[data-image-upload-input]")
            ?.click();
    });

    document.addEventListener("change", function (event) {
        if (event.target.matches("[data-image-upload-input]")) {
            handleSelection(event.target);
        }
    });

    function sync(root) {
        (root || document)
            .querySelectorAll("[data-image-upload]")
            .forEach(render);
    }

    window.CampusHubImageUpload = {sync: sync};
    document.addEventListener("DOMContentLoaded", function () {
        sync(document);
    });
})();
