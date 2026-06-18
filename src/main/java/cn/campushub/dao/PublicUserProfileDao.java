package cn.campushub.dao;

import cn.campushub.model.PublicUserProfile;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义公开用户主页数据访问能力及业务层依赖的数据契约。
 */
public interface PublicUserProfileDao {
    /**
     * 查询`ActiveById`。
     *
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<PublicUserProfile> findActiveById(long userId) throws SQLException;
}
