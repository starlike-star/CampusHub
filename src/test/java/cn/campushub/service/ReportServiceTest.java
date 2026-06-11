package cn.campushub.service;

import cn.campushub.dao.ReportDao;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 举报相关逻辑的正常路径、边界条件和失败场景。
 */
class ReportServiceTest {
    @Test
    void createsPendingReportForValidTarget() throws SQLException {
        FakeReportDao dao = new FakeReportDao();
        dao.ownerId = 9L;
        ReportService service = new ReportService(dao);

        ServiceResult<Void> result =
                service.create(7L, "12", "post", "该内容包含不实信息");

        assertTrue(result.success());
        assertEquals(1, dao.createCount);
        assertEquals("pending", dao.createdStatus);
    }

    @Test
    void rejectsOwnContent() throws SQLException {
        FakeReportDao dao = new FakeReportDao();
        dao.ownerId = 7L;
        ReportService service = new ReportService(dao);

        ServiceResult<Void> result =
                service.create(7L, "12", "goods", "测试举报原因");

        assertFalse(result.success());
        assertEquals("不能举报自己发布的内容", result.message());
        assertEquals(0, dao.createCount);
    }

    @Test
    void rejectsDuplicatePendingReport() throws SQLException {
        FakeReportDao dao = new FakeReportDao();
        dao.ownerId = 9L;
        dao.pending = true;
        ReportService service = new ReportService(dao);

        ServiceResult<Void> result =
                service.create(7L, "12", "comment", "重复举报测试");

        assertFalse(result.success());
        assertEquals(
                "你已经举报过该内容，请等待管理员处理",
                result.message()
        );
        assertEquals(0, dao.createCount);
    }

    @Test
    void validatesTypeTargetAndReason() throws SQLException {
        ReportService service = new ReportService(new FakeReportDao());

        assertFalse(service.create(7L, "0", "post", "有效原因").success());
        assertFalse(service.create(7L, "1", "activity", "有效原因").success());
        assertFalse(service.create(7L, "1", "post", "短").success());
    }

    private static class FakeReportDao implements ReportDao {
        private Long ownerId;
        private boolean pending;
        private int createCount;
        private String createdStatus;

        @Override
        public Long findTargetOwnerId(String targetType, long targetId) {
            return ownerId;
        }

        @Override
        public boolean existsPendingReport(
                long userId,
                String targetType,
                long targetId
        ) {
            return pending;
        }

        @Override
        public int createReport(
                long userId,
                String targetType,
                long targetId,
                String reason
        ) {
            createCount++;
            createdStatus = "pending";
            return 1;
        }
    }
}
