package cn.campushub.service;

import cn.campushub.dao.AdminDao;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AdminServiceTest {
    @Test
    void administratorCannotDisableOwnAccount() throws Exception {
        AtomicInteger updates = new AtomicInteger();
        AdminDao dao = proxyDao(updates);
        AdminService service = new AdminService(dao);

        ServiceResult<Void> result = service.updateUserStatus(7L, "7", "0");

        assertFalse(result.success());
        assertEquals("管理员不能禁用自己的账号", result.message());
        assertEquals(0, updates.get());
    }

    @Test
    void invalidContentStatusIsRejectedBeforeDaoCall() throws Exception {
        AtomicInteger updates = new AtomicInteger();
        AdminDao dao = proxyDao(updates);
        AdminService service = new AdminService(dao);

        ServiceResult<Void> result = service.updateGoodsStatus("9", "deleted");

        assertFalse(result.success());
        assertEquals("商品状态参数无效", result.message());
        assertEquals(0, updates.get());
    }

    private AdminDao proxyDao(AtomicInteger updates) {
        return (AdminDao) Proxy.newProxyInstance(
                AdminDao.class.getClassLoader(),
                new Class<?>[]{AdminDao.class},
                (proxy, method, args) -> {
                    if (method.getName().startsWith("update")) {
                        updates.incrementAndGet();
                    }
                    if (method.getReturnType() == boolean.class) {
                        return true;
                    }
                    return null;
                }
        );
    }
}
