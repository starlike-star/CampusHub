package cn.campushub.dao;

import cn.campushub.model.SearchResultVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 定义全站搜索数据访问能力及业务层依赖的数据契约。
 */
public interface SearchDao {
    /**
     * 统计`Matches`。
     *
     * @param keyword 搜索关键字
     * @return 按键组织的结果数据
     * @throws SQLException 数据库访问失败时抛出
     */
    Map<String, Integer> countMatches(String keyword) throws SQLException;

    /**
     * 搜索帖子列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<SearchResultVO> searchPosts(String keyword, int limit) throws SQLException;

    /**
     * 搜索商品。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<SearchResultVO> searchGoods(String keyword, int limit) throws SQLException;

    /**
     * 搜索`LostFound`。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<SearchResultVO> searchLostFound(String keyword, int limit) throws SQLException;

    /**
     * 搜索活动列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<SearchResultVO> searchActivities(String keyword, int limit) throws SQLException;

    /**
     * 搜索公告列表。
     *
     * @param keyword 搜索关键字
     * @param limit 查询数量上限
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    List<SearchResultVO> searchNotices(String keyword, int limit) throws SQLException;
}
