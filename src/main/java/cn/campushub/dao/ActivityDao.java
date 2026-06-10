package cn.campushub.dao;

import cn.campushub.model.Activity;
import cn.campushub.model.ActivityVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ActivityDao {
    List<ActivityVO> findAll(String status, String keyword, String sort)
            throws SQLException;

    Optional<ActivityVO> findById(long id) throws SQLException;

    long create(Activity activity) throws SQLException;

    boolean update(Activity activity, long userId, boolean admin)
            throws SQLException;

    boolean updateStatus(
            long id,
            long userId,
            boolean admin,
            String status
    ) throws SQLException;
}
