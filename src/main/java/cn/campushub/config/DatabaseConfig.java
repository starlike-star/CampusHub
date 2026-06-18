package cn.campushub.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 集中读取并校验数据库连接配置，为 JDBC 访问提供统一配置来源。
 */
public final class DatabaseConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("未找到 database.properties");
            }
            PROPERTIES.load(input);
            Class.forName(get(
                    "db.driver",
                    "CAMPUSHUB_DB_DRIVER",
                    "campushub.db.driver"
            ));
        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * 初始化`DatabaseConfig`对象及其运行所需依赖。
     */
    private DatabaseConfig() {
    }

    /**
     * 获取地址。
     *
     * @return 地址
     */
    public static String url() {
        return get("db.url", "CAMPUSHUB_DB_URL", "campushub.db.url");
    }

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public static String username() {
        return get("db.username", "CAMPUSHUB_DB_USERNAME", "campushub.db.username");
    }

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public static String password() {
        return get("db.password", "CAMPUSHUB_DB_PASSWORD", "campushub.db.password");
    }

    /**
     * 获取`DatabaseConfig`。
     *
     * @param propertyName 参数 `propertyName`
     * @param environmentName 环境变量名称
     * @param systemPropertyName JVM 系统属性名称
     * @return 读取到的配置值
     */
    private static String get(
            String propertyName,
            String environmentName,
            String systemPropertyName
    ) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        String systemPropertyValue = System.getProperty(systemPropertyName);
        if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
            return systemPropertyValue.trim();
        }
        String value = PROPERTIES.getProperty(propertyName);
        if (value == null || value.isBlank() || "PLEASE_SET_ENV".equals(value.trim())) {
            throw new IllegalStateException("缺少数据库配置：" + propertyName);
        }
        return value.trim();
    }
}
