// 处理注册表单校验、验证码刷新和密码可见性切换。
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerForm");
    const username = document.getElementById("username");
    const nickname = document.getElementById("nickname");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const usernameWarning = document.getElementById("usernameWarning");
    const nicknameWarning = document.getElementById("nicknameWarning");
    const passwordWarning = document.getElementById("passwordWarning");

    function setFieldState(input, warning, message) {
        const hasError = Boolean(message);
        input.classList.toggle("input-error", hasError);
        input.setCustomValidity(message);
        warning.textContent = message;
        warning.classList.toggle("show", hasError);
        return !hasError;
    }

    function validateNoWhitespace(input, warning, fieldName) {
        if (/\s/.test(input.value)) {
            return setFieldState(input, warning, fieldName + "不能包含空格字符");
        }
        return setFieldState(input, warning, "");
    }

    function validatePasswordMatch() {
        if (confirmPassword.value && password.value !== confirmPassword.value) {
            return setFieldState(confirmPassword, passwordWarning, "两次输入的密码不一致");
        }
        return setFieldState(confirmPassword, passwordWarning, "");
    }

    username.addEventListener("input", function () {
        validateNoWhitespace(username, usernameWarning, "用户名");
    });

    nickname.addEventListener("input", function () {
        validateNoWhitespace(nickname, nicknameWarning, "昵称");
    });

    password.addEventListener("input", validatePasswordMatch);
    confirmPassword.addEventListener("input", validatePasswordMatch);

    form.addEventListener("submit", function (event) {
        const usernameValid = validateNoWhitespace(username, usernameWarning, "用户名");
        const nicknameValid = validateNoWhitespace(nickname, nicknameWarning, "昵称");
        const passwordsMatch = validatePasswordMatch();

        if (!usernameValid || !nicknameValid || !passwordsMatch || !form.checkValidity()) {
            event.preventDefault();
            form.reportValidity();
        }
    });
});
