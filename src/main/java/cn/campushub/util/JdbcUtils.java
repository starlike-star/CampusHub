package cn.campushub.util;

import cn.campushub.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 统一创建数据库连接并处理 JDBC 资源相关基础操作。
 */
public final class JdbcUtils {
    /**
     * 初始化`Jdbc`对象及其运行所需依赖。
     */
    private JdbcUtils() {
    }

    /**
     * 获取`Connection`。
     *
     * @return `Connection`
     * @throws SQLException 数据库访问失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.url(),
                DatabaseConfig.username(),
                DatabaseConfig.password()
        );
    }
}
