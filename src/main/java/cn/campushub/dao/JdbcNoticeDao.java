package cn.campushub.dao;

import cn.campushub.model.Notice;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 使用 JDBC 实现公告数据的查询与持久化操作。
 */
public class JdbcNoticeDao implements NoticeDao {
    /**
     * 查询`VisibleNoticeById`。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    @Override
    public Optional<Notice> findVisibleNoticeById(long id) throws SQLException {
        String sql = """
                SELECT n.id, n.title, n.content, n.type, n.is_top,
                       n.created_by, n.created_at, n.updated_at,
                       u.nickname AS publisher_name
                FROM notices n
                LEFT JOIN users u ON u.id = n.created_by
                WHERE n.id = ? AND n.status = 1
                LIMIT 1
                """;
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                Notice notice = new Notice();
                notice.setId(resultSet.getLong("id"));
                notice.setTitle(resultSet.getString("title"));
                notice.setContent(resultSet.getString("content"));
                notice.setType(resultSet.getString("type"));
                notice.setTop(resultSet.getInt("is_top") == 1);
                long createdBy = resultSet.getLong("created_by");
                notice.setCreatedBy(resultSet.wasNull() ? null : createdBy);
                notice.setCreatedAt(toLocalDateTime(
                        resultSet.getTimestamp("created_at")
                ));
                notice.setUpdatedAt(toLocalDateTime(
                        resultSet.getTimestamp("updated_at")
                ));
                notice.setPublisherName(resultSet.getString("publisher_name"));
                return Optional.of(notice);
            }
        }
    }

    /**
     * 转换为`LocalDateTime`。
     *
     * @param timestamp 数据库时间戳
     * @return 方法处理结果
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
