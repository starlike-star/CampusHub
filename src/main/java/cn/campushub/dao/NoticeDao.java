package cn.campushub.dao;

import cn.campushub.model.Notice;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义公告数据访问能力及业务层依赖的数据契约。
 */
public interface NoticeDao {
    Optional<Notice> findVisibleNoticeById(long id) throws SQLException;
}
