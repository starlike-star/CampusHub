package cn.campushub.model;

import java.util.List;
import java.util.Map;

/**
 * 聚合全站搜索页面展示所需的数据。
 */
public record SearchPageVO(
        String keyword,
        String type,
        Map<String, Integer> counts,
        Map<String, List<SearchResultVO>> results,
        String validationMessage
) {
    public int count(String resultType) {
        return counts.getOrDefault(resultType, 0);
    }

    public int totalCount() {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<SearchResultVO> resultsFor(String resultType) {
        return results.getOrDefault(resultType, List.of());
    }

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
}
