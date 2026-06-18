package cn.campushub.service;

import cn.campushub.dao.AccountDao;
import cn.campushub.dao.JdbcAccountDao;
import cn.campushub.model.AccountCancelResult;
import cn.campushub.model.SessionUser;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;

/**
 * 编排账号业务规则、参数校验与数据访问操作。
 */
public class AccountService {
    private static final int MAX_REASON_LENGTH = 255;
    private static final int MAX_IP_LENGTH = 64;
    private static final int MAX_USER_AGENT_LENGTH = 255;

    private final AccountDao accountDao;

    /**
     * 初始化账号对象及其运行所需依赖。
     */
    public AccountService() {
        this(new JdbcAccountDao());
    }

    AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * 取消账号。
     *
     * @param user 用户数据
     * @param password 密码
     * @param reason 参数 `reason`
     * @param confirm 参数 `confirm`
     * @param ipAddress 参数 `ipAddress`
     * @param userAgent 参数 `userAgent`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public AccountCancelResult cancelAccount(
            SessionUser user,
            String password,
            String reason,
            String confirm,
            String ipAddress,
            String userAgent
    ) throws SQLException {
        if (user == null) {
            return AccountCancelResult.failure("请先登录");
        }
        if ("admin".equalsIgnoreCase(user.role())) {
            return AccountCancelResult.failure(
                    "管理员账号不能在前台注销，请先联系系统管理员。"
            );
        }
        if (password == null || password.isEmpty()) {
            return AccountCancelResult.failure("请输入当前密码");
        }
        if (!"true".equalsIgnoreCase(confirm)) {
            return AccountCancelResult.failure("请勾选注销确认");
        }
        reason = ValidationUtils.trimToNull(reason);
        if (reason != null && reason.length() > MAX_REASON_LENGTH) {
            return AccountCancelResult.failure("注销原因不能超过 255 个字符");
        }
        return accountDao.cancelAccount(
                user.id(),
                password,
                reason,
                truncate(ipAddress, MAX_IP_LENGTH),
                truncate(userAgent, MAX_USER_AGENT_LENGTH)
        );
    }

    /**
     * 判断是否`Active`。
     *
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    public boolean isActive(long userId) throws SQLException {
        return userId > 0 && accountDao.isActive(userId);
    }

    /**
     * 按长度限制截断账号。
     *
     * @param value 待处理的值
     * @param maximumLength 参数 `maximumLength`
     * @return 方法处理结果
     */
    private String truncate(String value, int maximumLength) {
        return value == null || value.length() <= maximumLength
                ? value
                : value.substring(0, maximumLength);
    }
}
