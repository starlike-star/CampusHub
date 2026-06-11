package cn.campushub.dao;

import cn.campushub.model.SearchResultVO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SearchDao {
    Map<String, Integer> countMatches(String keyword) throws SQLException;

    List<SearchResultVO> searchPosts(String keyword, int limit) throws SQLException;

    List<SearchResultVO> searchGoods(String keyword, int limit) throws SQLException;

    List<SearchResultVO> searchLostFound(String keyword, int limit) throws SQLException;

    List<SearchResultVO> searchActivities(String keyword, int limit) throws SQLException;

    List<SearchResultVO> searchNotices(String keyword, int limit) throws SQLException;
}
