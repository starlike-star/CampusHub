package cn.campushub.dao;

import cn.campushub.model.RememberToken;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 定义记住登录令牌数据访问能力及业务层依赖的数据契约。
 */
public interface RememberTokenDao {
    /**
     * 创建令牌。
     *
     * @param userId 用户编号
     * @param selector 参数 `selector`
     * @param tokenHash 参数 `tokenHash`
     * @param expiresAt 参数 `expiresAt`
     * @param userAgent 参数 `userAgent`
     * @param ipAddress 参数 `ipAddress`
     * @throws SQLException 数据库访问失败时抛出
     */
    void createToken(
            long userId,
            String selector,
            String tokenHash,
            LocalDateTime expiresAt,
            String userAgent,
            String ipAddress
    ) throws SQLException;

    /**
     * 根据`Selector`查询记住登录令牌。
     *
     * @param selector 参数 `selector`
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<RememberToken> findBySelector(String selector) throws SQLException;

    /**
     * 更新`LastUsed`。
     *
     * @param selector 参数 `selector`
     * @throws SQLException 数据库访问失败时抛出
     */
    void updateLastUsed(String selector) throws SQLException;

    /**
     * 删除`BySelector`。
     *
     * @param selector 参数 `selector`
     * @throws SQLException 数据库访问失败时抛出
     */
    void deleteBySelector(String selector) throws SQLException;

    /**
     * 删除`ExpiredTokens`。
     *
     * @return `deleteExpiredTokens`
     * @throws SQLException 数据库访问失败时抛出
     */
    int deleteExpiredTokens() throws SQLException;

    /**
     * 删除`ByUserId`。
     *
     * @param userId 用户编号
     * @throws SQLException 数据库访问失败时抛出
     */
    void deleteByUserId(long userId) throws SQLException;
}
