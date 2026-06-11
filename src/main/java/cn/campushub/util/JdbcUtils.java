package cn.campushub.util;

import cn.campushub.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 统一创建数据库连接并处理 JDBC 资源相关基础操作。
 */
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
