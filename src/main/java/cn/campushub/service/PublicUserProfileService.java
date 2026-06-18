package cn.campushub.service;

import cn.campushub.dao.JdbcPublicUserProfileDao;
import cn.campushub.dao.PublicUserProfileDao;
import cn.campushub.model.PublicUserProfile;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 编排公开用户主页业务规则、参数校验与数据访问操作。
 */
public class PublicUserProfileService {
    private final PublicUserProfileDao profileDao;

    /**
     * 初始化公开用户个人资料对象及其运行所需依赖。
     */
    public PublicUserProfileService() {
        this(new JdbcPublicUserProfileDao());
    }

    PublicUserProfileService(PublicUserProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    /**
     * 查询公开用户个人资料。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<PublicUserProfile> find(long userId) throws SQLException {
        return userId > 0 ? profileDao.findActiveById(userId) : Optional.empty();
    }
}
