package cn.campushub.dao;

import cn.campushub.model.Notice;

import java.sql.SQLException;
import java.util.Optional;

public interface NoticeDao {
    Optional<Notice> findVisibleNoticeById(long id) throws SQLException;
}
