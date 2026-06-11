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

    public PublicUserProfileService() {
        this(new JdbcPublicUserProfileDao());
    }

    PublicUserProfileService(PublicUserProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public Optional<PublicUserProfile> find(long userId) throws SQLException {
        return userId > 0 ? profileDao.findActiveById(userId) : Optional.empty();
    }
}
