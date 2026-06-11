package cn.campushub.dao;

import cn.campushub.model.AccountCancelResult;

import java.sql.SQLException;

/**
 * 定义账号数据访问能力及业务层依赖的数据契约。
 */
public interface AccountDao {
    AccountCancelResult cancelAccount(
            long userId,
            String password,
            String reason,
            String ipAddress,
            String userAgent
    ) throws SQLException;

    boolean isActive(long userId) throws SQLException;
}
