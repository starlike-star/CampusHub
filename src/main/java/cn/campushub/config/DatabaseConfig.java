package cn.campushub.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("未找到 database.properties");
            }
            PROPERTIES.load(input);
            Class.forName(get("db.driver", "CAMPUSHUB_DB_DRIVER"));
        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DatabaseConfig() {
    }

    public static String url() {
        return get("db.url", "CAMPUSHUB_DB_URL");
    }

    public static String username() {
        return get("db.username", "CAMPUSHUB_DB_USERNAME");
    }

    public static String password() {
        return get("db.password", "CAMPUSHUB_DB_PASSWORD");
    }

    private static String get(String propertyName, String environmentName) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        String value = PROPERTIES.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("缺少数据库配置：" + propertyName);
        }
        return value.trim();
    }
}
