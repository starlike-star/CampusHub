package cn.campushub.service;

import cn.campushub.dao.JdbcSearchDao;
import cn.campushub.dao.SearchDao;
import cn.campushub.model.SearchPageVO;
import cn.campushub.model.SearchResultVO;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchService {
    private static final List<String> RESULT_TYPES =
            List.of("post", "goods", "lost_found", "activity", "notice");
    private static final Set<String> ALLOWED_TYPES =
            Set.of("all", "post", "goods", "lost_found", "activity", "notice");
    private static final int ALL_LIMIT = 5;
    private static final int SINGLE_TYPE_LIMIT = 20;
    private static final int MAX_KEYWORD_LENGTH = 50;

    private final SearchDao searchDao;

    public SearchService() {
        this(new JdbcSearchDao());
    }

    SearchService(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public SearchPageVO search(String keywordValue, String typeValue)
            throws SQLException {
        String keyword = normalizeKeyword(keywordValue);
        String type = normalizeType(typeValue);
        if (keyword == null) {
            return emptyPage(
                    "",
                    type,
                    "请输入关键词进行搜索"
            );
        }
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            return emptyPage(
                    keyword,
                    type,
                    "搜索关键词不能超过 50 个字符"
            );
        }

        Map<String, Integer> counts = searchDao.countMatches(keyword);
        Map<String, List<SearchResultVO>> results = emptyResults();
        if ("all".equals(type)) {
            results.put("post", searchDao.searchPosts(keyword, ALL_LIMIT));
            results.put("goods", searchDao.searchGoods(keyword, ALL_LIMIT));
            results.put(
                    "lost_found",
                    searchDao.searchLostFound(keyword, ALL_LIMIT)
            );
            results.put(
                    "activity",
                    searchDao.searchActivities(keyword, ALL_LIMIT)
            );
            results.put("notice", searchDao.searchNotices(keyword, ALL_LIMIT));
        } else {
            results.put(type, searchType(keyword, type));
        }
        return new SearchPageVO(keyword, type, counts, results, null);
    }

    public String normalizeType(String value) {
        return value != null && ALLOWED_TYPES.contains(value) ? value : "all";
    }

    private List<SearchResultVO> searchType(String keyword, String type)
            throws SQLException {
        return switch (type) {
            case "post" -> searchDao.searchPosts(keyword, SINGLE_TYPE_LIMIT);
            case "goods" -> searchDao.searchGoods(keyword, SINGLE_TYPE_LIMIT);
            case "lost_found" ->
                    searchDao.searchLostFound(keyword, SINGLE_TYPE_LIMIT);
            case "activity" ->
                    searchDao.searchActivities(keyword, SINGLE_TYPE_LIMIT);
            case "notice" ->
                    searchDao.searchNotices(keyword, SINGLE_TYPE_LIMIT);
            default -> List.of();
        };
    }

    private String normalizeKeyword(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private SearchPageVO emptyPage(
            String keyword,
            String type,
            String validationMessage
    ) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        RESULT_TYPES.forEach(resultType -> counts.put(resultType, 0));
        return new SearchPageVO(
                keyword,
                type,
                counts,
                emptyResults(),
                validationMessage
        );
    }

    private Map<String, List<SearchResultVO>> emptyResults() {
        Map<String, List<SearchResultVO>> results = new LinkedHashMap<>();
        RESULT_TYPES.forEach(type -> results.put(type, List.of()));
        return results;
    }
}
