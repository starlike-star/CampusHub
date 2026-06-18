package cn.campushub.dao;

import cn.campushub.model.PrivateConversation;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 定义私信会话数据访问能力及业务层依赖的数据契约。
 */
public interface PrivateConversationDao {
    /**
     * 获取`OrCreate`。
     *
     * @param userAId `userA`编号
     * @param userBId `userB`编号
     * @return `OrCreate`
     * @throws SQLException 数据库访问失败时抛出
     */
    long getOrCreate(long userAId, long userBId) throws SQLException;

    /**
     * 根据`IdForUser`查询`PrivateConversation`。
     *
     * @param conversationId 会话编号
     * @param userId 用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<PrivateConversation> findByIdForUser(long conversationId, long userId)
            throws SQLException;

    /**
     * 根据用户查询`PrivateConversation`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<PrivateConversation> findByUser(long userId) throws SQLException;

    /**
     * 判断是否`ActiveUser`。
     *
     * @param userId 用户编号
     * @return 满足条件或操作成功时返回 true，否则返回 false
     * @throws SQLException 数据库访问失败时抛出
     */
    boolean isActiveUser(long userId) throws SQLException;
}
