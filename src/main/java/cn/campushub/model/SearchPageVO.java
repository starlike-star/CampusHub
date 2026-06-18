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
    /**
     * 统计`SearchPageVO`。
     *
     * @param resultType 参数 `resultType`
     * @return 方法处理结果
     */
    public int count(String resultType) {
        return counts.getOrDefault(resultType, 0);
    }

    /**
     * 转换为`talCount`。
     *
     * @return `totalCount`
     */
    public int totalCount() {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 查询`resultsFor`并返回结果。
     *
     * @param resultType 参数 `resultType`
     * @return 符合条件的数据列表
     */
    public List<SearchResultVO> resultsFor(String resultType) {
        return results.getOrDefault(resultType, List.of());
    }

    /**
     * 判断是否具有关键字。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
}
