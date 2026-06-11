package cn.campushub.dao;

import cn.campushub.model.ClaimCreateResult;
import cn.campushub.model.ClaimHandleResult;
import cn.campushub.model.ClaimRequest;
import cn.campushub.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 使用 JDBC 实现认领申请数据的查询与持久化操作。
 */
public class JdbcClaimRequestDao implements ClaimRequestDao {
    @Override
    public ClaimCreateResult create(
            long lostFoundId,
            long userId,
            String message,
            String contact
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ClaimTarget target = lockTarget(connection, lostFoundId);
                if (target == null
                        || target.ownerId == userId
                        || !("pending".equals(target.status)
                        || "claiming".equals(target.status))) {
                    connection.rollback();
                    return null;
                }
                if (hasPending(connection, lostFoundId, userId)) {
                    connection.rollback();
                    return new ClaimCreateResult(false, target.ownerId, target.title);
                }
                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO claim_requests
                            (lost_found_id, user_id, message, contact, status)
                        VALUES (?, ?, ?, ?, 'pending')
                        """)) {
                    statement.setLong(1, lostFoundId);
                    statement.setLong(2, userId);
                    statement.setString(3, message);
                    statement.setString(4, contact);
                    statement.executeUpdate();
                }
                if ("pending".equals(target.status)) {
                    try (PreparedStatement statement = connection.prepareStatement("""
                            UPDATE lost_found SET status = 'claiming' WHERE id = ?
                            """)) {
                        statement.setLong(1, lostFoundId);
                        statement.executeUpdate();
                    }
                }
                connection.commit();
                return new ClaimCreateResult(true, target.ownerId, target.title);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<ClaimRequest> findByLostFound(long lostFoundId, long ownerId)
            throws SQLException {
        String sql = """
                SELECT cr.id, cr.lost_found_id, cr.user_id, cr.message,
                       cr.contact, cr.status, cr.created_at, cr.handled_at,
                       u.nickname AS applicant_nickname,
                       u.avatar AS applicant_avatar,
                       u.college AS applicant_college
                FROM claim_requests cr
                JOIN lost_found lf ON lf.id = cr.lost_found_id
                JOIN users u ON u.id = cr.user_id
                WHERE cr.lost_found_id = ? AND lf.user_id = ?
                ORDER BY cr.created_at DESC, cr.id DESC
                """;
        List<ClaimRequest> result = new ArrayList<>();
        try (Connection connection = JdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, lostFoundId);
            statement.setLong(2, ownerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(map(resultSet));
                }
            }
        }
        return result;
    }

    @Override
    public Optional<ClaimHandleResult> handle(
            long claimId,
            long ownerId,
            String action
    ) throws SQLException {
        try (Connection connection = JdbcUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                HandleTarget target = lockClaim(connection, claimId, ownerId);
                if (target == null || !"pending".equals(target.claimStatus)) {
                    connection.rollback();
                    return Optional.empty();
                }
                boolean approved = "approve".equals(action);
                updateClaim(connection, claimId, approved ? "approved" : "rejected");
                if (approved) {
                    rejectOtherClaims(connection, target.lostFoundId, claimId);
                    updateLostFoundStatus(
                            connection,
                            target.lostFoundId,
                            "completed"
                    );
                } else if (!hasOtherPending(connection, target.lostFoundId)) {
                    restorePending(connection, target.lostFoundId);
                }
                connection.commit();
                return Optional.of(new ClaimHandleResult(
                        target.applicantId,
                        target.title,
                        approved
                ));
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private ClaimTarget lockTarget(Connection connection, long id)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT user_id, title, status
                FROM lost_found WHERE id = ? FOR UPDATE
                """)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? new ClaimTarget(
                                resultSet.getLong("user_id"),
                                resultSet.getString("title"),
                                resultSet.getString("status")
                        )
                        : null;
            }
        }
    }

    private boolean hasPending(Connection connection, long lostFoundId, long userId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1 FROM claim_requests
                WHERE lost_found_id = ? AND user_id = ? AND status = 'pending'
                LIMIT 1
                """)) {
            statement.setLong(1, lostFoundId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private HandleTarget lockClaim(Connection connection, long claimId, long ownerId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT cr.lost_found_id, cr.user_id, cr.status AS claim_status,
                       lf.title
                FROM claim_requests cr
                JOIN lost_found lf ON lf.id = cr.lost_found_id
                WHERE cr.id = ? AND lf.user_id = ?
                FOR UPDATE
                """)) {
            statement.setLong(1, claimId);
            statement.setLong(2, ownerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? new HandleTarget(
                                resultSet.getLong("lost_found_id"),
                                resultSet.getLong("user_id"),
                                resultSet.getString("claim_status"),
                                resultSet.getString("title")
                        )
                        : null;
            }
        }
    }

    private void updateClaim(Connection connection, long claimId, String status)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE claim_requests
                SET status = ?, handled_at = NOW()
                WHERE id = ?
                """)) {
            statement.setString(1, status);
            statement.setLong(2, claimId);
            statement.executeUpdate();
        }
    }

    private void rejectOtherClaims(
            Connection connection,
            long lostFoundId,
            long approvedId
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE claim_requests
                SET status = 'rejected', handled_at = NOW()
                WHERE lost_found_id = ? AND id != ? AND status = 'pending'
                """)) {
            statement.setLong(1, lostFoundId);
            statement.setLong(2, approvedId);
            statement.executeUpdate();
        }
    }

    private boolean hasOtherPending(Connection connection, long lostFoundId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1 FROM claim_requests
                WHERE lost_found_id = ? AND status = 'pending' LIMIT 1
                """)) {
            statement.setLong(1, lostFoundId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void restorePending(Connection connection, long lostFoundId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE lost_found SET status = 'pending'
                WHERE id = ? AND status = 'claiming'
                """)) {
            statement.setLong(1, lostFoundId);
            statement.executeUpdate();
        }
    }

    private void updateLostFoundStatus(
            Connection connection,
            long lostFoundId,
            String status
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE lost_found SET status = ? WHERE id = ?
                """)) {
            statement.setString(1, status);
            statement.setLong(2, lostFoundId);
            statement.executeUpdate();
        }
    }

    private ClaimRequest map(ResultSet resultSet) throws SQLException {
        ClaimRequest request = new ClaimRequest();
        request.setId(resultSet.getLong("id"));
        request.setLostFoundId(resultSet.getLong("lost_found_id"));
        request.setUserId(resultSet.getLong("user_id"));
        request.setMessage(resultSet.getString("message"));
        request.setContact(resultSet.getString("contact"));
        request.setStatus(resultSet.getString("status"));
        request.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        request.setHandledAt(toLocalDateTime(resultSet.getTimestamp("handled_at")));
        request.setApplicantNickname(resultSet.getString("applicant_nickname"));
        request.setApplicantAvatar(resultSet.getString("applicant_avatar"));
        request.setApplicantCollege(resultSet.getString("applicant_college"));
        return request;
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    private record ClaimTarget(long ownerId, String title, String status) {
    }

    private record HandleTarget(
            long lostFoundId,
            long applicantId,
            String claimStatus,
            String title
    ) {
    }
}
