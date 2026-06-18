package cn.campushub.dao;

import cn.campushub.model.PrivateMessage;

import java.sql.SQLException;
import java.util.List;

/**
 * 定义私信消息数据访问能力及业务层依赖的数据契约。
 */
public interface PrivateMessageDao {
    /**
     * 创建`PrivateMessage`。
     *
     * @param conversationId 会话编号
     * @param senderId `sender`编号
     * @param receiverId `receiver`编号
     * @param content 正文内容
     * @return 新建数据的编号
     * @throws SQLException 数据库访问失败时抛出
     */
    long create(
            long conversationId,
            long senderId,
            long receiverId,
            String content
    ) throws SQLException;

    /**
     * 根据会话查询`PrivateMessage`。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<PrivateMessage> findByConversation(long conversationId, long userId)
            throws SQLException;

    /**
     * 标记会话已读状态。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int markConversationRead(long conversationId, long userId)
            throws SQLException;

    /**
     * 统计未读。
     *
     * @param userId 用户编号
     * @return 方法处理结果
     * @throws SQLException 数据库访问失败时抛出
     */
    int countUnread(long userId) throws SQLException;
}
