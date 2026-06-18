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
    /**
     * 创建消息。
     *
     * @param message 消息数据
     * @throws SQLException 数据库访问失败时抛出
     */
    void create(Message message) throws SQLException;

    /**
     * 查询`ActiveAdminIds`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Long> findActiveAdminIds() throws SQLException;

    /**
     * 根据用户查询消息。
     *
     * @param userId 用户编号
     * @param type 参数 `type`
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<Message> findByUser(long userId, String type) throws SQLException;

    /**
     * 统计`ByUser`。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int countByUser(long userId) throws SQLException;

    /**
     * 统计未读。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int countUnread(long userId) throws SQLException;

    /**
     * 标记已读状态。
     *
     * @param userId 用户编号
     * @param messageId 消息编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean markRead(long userId, long messageId) throws SQLException;

    /**
     * 标记全部数据已读状态。
     *
     * @param userId 用户编号
     * @param type 参数 `type`
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int markAllRead(long userId, String type) throws SQLException;

    /**
     * 查询`PostTarget`。
     *
     * @param postId 帖子编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<NotificationTarget> findPostTarget(long postId) throws SQLException;

    /**
     * 查询`GoodsTarget`。
     *
     * @param goodsId 商品编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<NotificationTarget> findGoodsTarget(long goodsId) throws SQLException;

    /**
     * 查询`CommentTarget`。
     *
     * @param commentId 评论编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<NotificationTarget> findCommentTarget(long commentId) throws SQLException;
}
