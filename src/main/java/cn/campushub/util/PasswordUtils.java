package cn.campushub.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 提供密码哈希生成与安全比对能力。
 */
public final class PasswordUtils {
    private static final int LOG_ROUNDS = 12;

    private PasswordUtils() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean matches(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, storedPassword);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
