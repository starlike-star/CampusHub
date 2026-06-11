package cn.campushub.service;

import cn.campushub.dao.JdbcNoticeDao;
import cn.campushub.dao.NoticeDao;
import cn.campushub.model.Notice;

import java.sql.SQLException;
import java.util.Optional;

public class NoticeService {
    private final NoticeDao noticeDao;

    public NoticeService() {
        this(new JdbcNoticeDao());
    }

    NoticeService(NoticeDao noticeDao) {
        this.noticeDao = noticeDao;
    }

    public Optional<Notice> findVisibleNoticeById(long id) throws SQLException {
        return id > 0 ? noticeDao.findVisibleNoticeById(id) : Optional.empty();
    }
}
