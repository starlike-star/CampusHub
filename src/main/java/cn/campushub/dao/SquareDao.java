package cn.campushub.dao;

import cn.campushub.model.Notice;
import cn.campushub.model.Post;

import java.sql.SQLException;
import java.util.List;

public interface SquareDao {
    List<Post> findPosts(String tab, Long currentUserId, String keyword)
            throws SQLException;

    List<Notice> findNotices(String keyword) throws SQLException;
}
