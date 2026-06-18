package cn.campushub.service;

import cn.campushub.dao.JdbcNoticeDao;
import cn.campushub.dao.NoticeDao;
import cn.campushub.model.Notice;

import java.sql.SQLException;
import java.util.Optional;

/**
 * 编排公告业务规则、参数校验与数据访问操作。
 */
public class NoticeService {
    private final NoticeDao noticeDao;

    /**
     * 初始化公告对象及其运行所需依赖。
     */
    public NoticeService() {
        this(new JdbcNoticeDao());
    }

    NoticeService(NoticeDao noticeDao) {
        this.noticeDao = noticeDao;
    }

    /**
     * 查询`VisibleNoticeById`。
     *
     * @param id 业务数据编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<Notice> findVisibleNoticeById(long id) throws SQLException {
        return id > 0 ? noticeDao.findVisibleNoticeById(id) : Optional.empty();
    }
}
