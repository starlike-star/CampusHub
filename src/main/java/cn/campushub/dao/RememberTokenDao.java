package cn.campushub.dao;

import cn.campushub.model.RememberToken;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 定义记住登录令牌数据访问能力及业务层依赖的数据契约。
 */
public interface RememberTokenDao {
    void createToken(
            long userId,
            String selector,
            String tokenHash,
            LocalDateTime expiresAt,
            String userAgent,
            String ipAddress
    ) throws SQLException;

    Optional<RememberToken> findBySelector(String selector) throws SQLException;

    void updateLastUsed(String selector) throws SQLException;

    void deleteBySelector(String selector) throws SQLException;

    int deleteExpiredTokens() throws SQLException;

    void deleteByUserId(long userId) throws SQLException;
}
