package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.LostFound;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义失物招领数据访问能力及业务层依赖的数据契约。
 */
public interface LostFoundDao {
    List<LostFound> findAll(
            String type,
            String status,
            String keyword,
            Long categoryId,
            String sort
    ) throws SQLException;

    List<LostFound> findByUser(long userId) throws SQLException;

    List<Category> findActiveCategories() throws SQLException;

    boolean isActiveCategory(long categoryId) throws SQLException;

    long create(LostFound lostFound) throws SQLException;

    Optional<LostFound> findById(long id) throws SQLException;

    boolean update(LostFound lostFound, boolean admin) throws SQLException;

    boolean updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException;
}
