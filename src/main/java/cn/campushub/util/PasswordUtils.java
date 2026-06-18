package cn.campushub.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 提供密码哈希生成与安全比对能力。
 */
public final class PasswordUtils {
    private static final int LOG_ROUNDS = 12;

    /**
     * 初始化密码对象及其运行所需依赖。
     */
    private PasswordUtils() {
    }

    /**
     * 判断是否具有`h`。
     *
     * @param plainPassword 参数 `plainPassword`
     * @return 方法处理结果
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * 根据输入计算并返回 `matches` 的处理结果。
     *
     * @param plainPassword 参数 `plainPassword`
     * @param storedPassword 参数 `storedPassword`
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
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
