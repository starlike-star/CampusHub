package cn.campushub.dao;

import cn.campushub.model.Notice;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 定义公告数据访问能力及业务层依赖的数据契约。
 */
public interface NoticeDao {
    /**
     * 查询`VisibleNoticeById`。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    Optional<Notice> findVisibleNoticeById(long id) throws SQLException;
}
