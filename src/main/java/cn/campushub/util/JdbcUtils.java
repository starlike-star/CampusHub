package cn.campushub.util;

import cn.campushub.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcUtils {
    private JdbcUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.url(),
                DatabaseConfig.username(),
                DatabaseConfig.password()
        );
    }
}
