package cn.campushub.dao;

import cn.campushub.model.Notice;
import cn.campushub.model.Post;

import java.sql.SQLException;
import java.util.List;

/**
 * 定义校园广场数据访问能力及业务层依赖的数据契约。
 */
public interface SquareDao {
    /**
     * 查询帖子列表。
     *
     * @param tab 参数 `tab`
     * @param currentUserId 当前用户编号
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Post> findPosts(String tab, Long currentUserId, String keyword)
            throws SQLException;

    /**
     * 查询公告列表。
     *
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Notice> findNotices(String keyword) throws SQLException;
}
