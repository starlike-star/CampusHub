package cn.campushub.dao;

import cn.campushub.model.AccountCancelResult;

import java.sql.SQLException;

/**
 * 定义账号数据访问能力及业务层依赖的数据契约。
 */
public interface AccountDao {
    /**
     * 取消账号。
     *
     * @param userId 用户编号
     * @param password 密码
     * @param reason 参数 `reason`
     * @param ipAddress 参数 `ipAddress`
     * @param userAgent 参数 `userAgent`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    AccountCancelResult cancelAccount(
            long userId,
            String password,
            String reason,
            String ipAddress,
            String userAgent
    ) throws SQLException;

    /**
     * 判断是否`Active`。
     *
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isActive(long userId) throws SQLException;
}
