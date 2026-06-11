package cn.campushub.dao;

import cn.campushub.model.AccountCancelResult;

import java.sql.SQLException;

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
