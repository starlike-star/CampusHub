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

    public AccountService() {
        this(new JdbcAccountDao());
    }

    AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

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

    public boolean isActive(long userId) throws SQLException {
        return userId > 0 && accountDao.isActive(userId);
    }

    private String truncate(String value, int maximumLength) {
        return value == null || value.length() <= maximumLength
                ? value
                : value.substring(0, maximumLength);
    }
}
