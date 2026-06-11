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

    private ValidationUtils() {
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 100 && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidNickname(String nickname) {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72) {
            return false;
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

    public static boolean containsWhitespace(String value) {
        return value != null && value.codePoints().anyMatch(Character::isWhitespace);
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
