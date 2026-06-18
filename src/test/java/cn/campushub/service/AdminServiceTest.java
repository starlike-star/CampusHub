package cn.campushub.service;

import cn.campushub.dao.AdminDao;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 验证 后台管理相关逻辑的正常路径、边界条件和失败场景。
 */
class AdminServiceTest {
    /**
     * 验证 `administratorCannotDisableOwnAccount` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

    /**
     * 验证 `invalidContentStatusIsRejectedBeforeDaoCall` 场景下的业务行为与预期结果一致。
     *
     * @throws Exception 处理过程中发生该异常时抛出
     */
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

    /**
     * 验证 `proxyDao` 场景下的业务行为与预期结果一致。
     *
     * @param updates 参数 `updates`
     * @return 方法处理结果
     */
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
