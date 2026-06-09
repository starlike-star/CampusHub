package cn.campushub.service;

import cn.campushub.dao.JdbcSquareDao;
import cn.campushub.dao.SquareDao;
import cn.campushub.model.Notice;
import cn.campushub.model.Post;
import cn.campushub.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class SquareService {
    private static final Set<String> POST_TABS =
            Set.of("latest", "hot", "study", "life");
    private static final Set<String> ALL_TABS =
            Set.of("latest", "hot", "notice", "study", "life");

    private final SquareDao squareDao;

    public SquareService() {
        this(new JdbcSquareDao());
    }

    SquareService(SquareDao squareDao) {
        this.squareDao = squareDao;
    }

    public String normalizeTab(String tab) {
        return tab != null && ALL_TABS.contains(tab) ? tab : "latest";
    }

    public String normalizeKeyword(String keyword) {
        keyword = ValidationUtils.trimToNull(keyword);
        if (keyword == null) {
            return null;
        }
        return keyword.length() <= 100 ? keyword : keyword.substring(0, 100);
    }

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

    public List<Notice> listNotices(String keyword) throws SQLException {
        return squareDao.findNotices(normalizeKeyword(keyword));
    }
}
