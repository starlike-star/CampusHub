// 根据输入内容自动调整多行文本框高度。
(function () {
    function resizeTextarea(textarea) {
        if (!textarea
                || textarea.hidden
                || textarea.offsetParent === null
                || textarea.classList.contains("post-raw-content")) {
            return;
        }
        textarea.classList.add("auto-resize");
        textarea.style.height = "auto";
        textarea.style.height = textarea.scrollHeight + "px";
    }

    function initializeTextareas(root) {
        root.querySelectorAll("textarea:not([hidden]):not(.post-raw-content)")
                .forEach(resizeTextarea);
    }

    document.addEventListener("DOMContentLoaded", function () {
        initializeTextareas(document);
    });

    document.addEventListener("input", function (event) {
        if (event.target.matches("textarea:not([hidden]):not(.post-raw-content)")) {
            resizeTextarea(event.target);
        }
    });

    window.resizeTextarea = resizeTextarea;
    window.initializeTextareas = initializeTextareas;
})();
