package cn.campushub.dao;

import cn.campushub.model.Message;
import cn.campushub.model.NotificationTarget;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义站内通知数据访问能力及业务层依赖的数据契约。
 */
public interface MessageDao {
    void create(Message message) throws SQLException;

    List<Long> findActiveAdminIds() throws SQLException;

    List<Message> findByUser(long userId, String type) throws SQLException;

    int countByUser(long userId) throws SQLException;

    int countUnread(long userId) throws SQLException;

    boolean markRead(long userId, long messageId) throws SQLException;

    int markAllRead(long userId, String type) throws SQLException;

    Optional<NotificationTarget> findPostTarget(long postId) throws SQLException;

    Optional<NotificationTarget> findGoodsTarget(long goodsId) throws SQLException;

    Optional<NotificationTarget> findCommentTarget(long commentId) throws SQLException;
}
