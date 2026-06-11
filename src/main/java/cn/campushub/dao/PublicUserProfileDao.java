package cn.campushub.dao;

import cn.campushub.model.PublicUserProfile;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义公开用户主页数据访问能力及业务层依赖的数据契约。
 */
public interface PublicUserProfileDao {
    Optional<PublicUserProfile> findActiveById(long userId) throws SQLException;
}
