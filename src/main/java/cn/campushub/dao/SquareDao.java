package cn.campushub.dao;

import cn.campushub.model.Notice;
import cn.campushub.model.Post;

import java.sql.SQLException;
import java.util.List;

/**
 * 定义校园广场数据访问能力及业务层依赖的数据契约。
 */
public interface SquareDao {
    List<Post> findPosts(String tab, Long currentUserId, String keyword)
            throws SQLException;

    List<Notice> findNotices(String keyword) throws SQLException;
}
