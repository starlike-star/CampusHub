package cn.campushub.service;

import cn.campushub.dao.JdbcSquareDao;
import cn.campushub.dao.SquareDao;
import cn.campushub.model.Notice;
import cn.campushub.model.Post;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * 编排校园广场业务规则、参数校验与数据访问操作。
 */
public class SquareService {
    private static final Set<String> POST_TABS =
            Set.of("latest", "hot", "study", "life", "trade");
    private static final Set<String> ALL_TABS =
            Set.of("latest", "hot", "notice", "study", "life", "trade");

    private final SquareDao squareDao;

    /**
     * 初始化`Square`对象及其运行所需依赖。
     */
    public SquareService() {
        this(new JdbcSquareDao());
    }

    SquareService(SquareDao squareDao) {
        this.squareDao = squareDao;
    }

    /**
     * 规范化`Tab`。
     *
     * @param tab 参数 `tab`
     * @return 方法处理结果
     */
    public String normalizeTab(String tab) {
        return tab != null && ALL_TABS.contains(tab) ? tab : "latest";
    }

    /**
     * 规范化关键字。
     *
     * @param keyword 搜索关键字
     * @return 方法处理结果
     */
    public String normalizeKeyword(String keyword) {
        keyword = ValidationUtils.trimToNull(keyword);
        if (keyword == null) {
            return null;
        }
        return keyword.length() <= 100 ? keyword : keyword.substring(0, 100);
    }

    /**
     * 查询帖子列表。
     *
     * @param tab 参数 `tab`
     * @param currentUserId 当前用户编号
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Post> listPosts(
            String tab,
            Long currentUserId,
            String keyword
    ) throws SQLException {
        String normalizedTab = normalizeTab(tab);
        if (!POST_TABS.contains(normalizedTab)) {
            return List.of();
        }
        return squareDao.findPosts(
                normalizedTab,
                currentUserId,
                normalizeKeyword(keyword)
        );
    }

    /**
     * 查询公告列表。
     *
     * @param keyword 搜索关键字
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Notice> listNotices(String keyword) throws SQLException {
        return squareDao.findNotices(normalizeKeyword(keyword));
    }
}
