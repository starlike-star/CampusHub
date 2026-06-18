package cn.campushub.util;

import java.util.regex.Pattern;

/**
 * 集中提供常用文本、数字和业务参数校验方法。
 */
public final class ValidationUtils {
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]{3,49}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N}_\\-·]{2,50}$");

    /**
     * 初始化`Validation`对象及其运行所需依赖。
     */
    private ValidationUtils() {
    }

    /**
     * 判断是否`ValidUsername`。
     *
     * @param username 用户名
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 判断是否`ValidEmail`。
     *
     * @param email 电子邮箱
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 100 && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 判断是否`ValidNickname`。
     *
     * @param nickname 用户昵称
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    /**
     * 判断是否`ValidPassword`。
     *
     * @param password 密码
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72) {
            return false;
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

    /**
     * 根据输入计算并返回 `containsWhitespace` 的处理结果。
     *
     * @param value 待处理的值
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean containsWhitespace(String value) {
        return value != null && value.codePoints().anyMatch(Character::isWhitespace);
    }

    /**
     * 根据输入计算并返回 `trimToNull` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
