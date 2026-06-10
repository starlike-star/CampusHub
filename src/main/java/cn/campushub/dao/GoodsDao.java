package cn.campushub.dao;

import cn.campushub.model.Category;
import cn.campushub.model.Goods;
import cn.campushub.model.PostToggleResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GoodsDao {
    List<Goods> findGoods(
            Long currentUserId,
            String keyword,
            Long categoryId,
            String status,
            String tradeMethod,
            String sort
    ) throws SQLException;

    List<Goods> findOwnGoods(long userId) throws SQLException;

    List<Goods> findFavoriteGoods(long userId) throws SQLException;

    List<Category> findActiveGoodsCategories() throws SQLException;

    boolean isActiveGoodsCategory(long categoryId) throws SQLException;

    long create(Goods goods) throws SQLException;

    Optional<Goods> findVisibleById(long goodsId, Long currentUserId)
            throws SQLException;

    PostToggleResult toggleFavorite(long goodsId, long userId)
            throws SQLException;

    Optional<Goods> update(Goods goods, boolean admin) throws SQLException;

    boolean updateStatus(
            long goodsId,
            long userId,
            boolean admin,
            String status
    ) throws SQLException;
}
