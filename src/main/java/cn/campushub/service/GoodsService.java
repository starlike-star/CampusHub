package cn.campushub.service;

import cn.campushub.dao.GoodsDao;
import cn.campushub.dao.JdbcGoodsDao;
import cn.campushub.model.Category;
import cn.campushub.model.Goods;
import cn.campushub.model.PostToggleResult;
import cn.campushub.util.ValidationUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GoodsService {
    private static final Set<String> STATUSES =
            Set.of("on_sale", "reserved", "sold", "off_shelf");
    private static final Set<String> LIST_STATUSES =
            Set.of("on_sale", "reserved", "sold");
    private static final Set<String> SORTS =
            Set.of("latest", "price_asc", "price_desc", "hot");
    private static final Set<String> TRADE_METHODS =
            Set.of("offline", "online", "both");
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");

    private final GoodsDao goodsDao;

    public GoodsService() {
        this(new JdbcGoodsDao());
    }

    GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public List<Goods> list(
            Long currentUserId,
            String keyword,
            String categoryIdValue,
            String status,
            String tradeMethod,
            String sort
    ) throws SQLException {
        Long categoryId = parseOptionalPositiveLong(categoryIdValue);
        if (categoryId != null && !goodsDao.isActiveGoodsCategory(categoryId)) {
            categoryId = null;
        }
        return goodsDao.findGoods(
                currentUserId,
                normalizeKeyword(keyword),
                categoryId,
                normalizeListStatus(status),
                normalizeTradeMethodFilter(tradeMethod),
                normalizeSort(sort)
        );
    }

    public List<Goods> listOwnGoods(long userId) throws SQLException {
        return goodsDao.findOwnGoods(userId);
    }

    public List<Goods> listFavoriteGoods(long userId) throws SQLException {
        return goodsDao.findFavoriteGoods(userId);
    }

    public List<Category> listCategories() throws SQLException {
        return goodsDao.findActiveGoodsCategories();
    }

    public Optional<Goods> detail(long goodsId, Long currentUserId)
            throws SQLException {
        if (goodsId <= 0) {
            return Optional.empty();
        }
        return goodsDao.findVisibleById(goodsId, currentUserId);
    }

    public ServiceResult<Long> create(
            long userId,
            String title,
            String description,
            String priceValue,
            String categoryIdValue,
            String conditionLevel,
            String images,
            String tradePlace,
            String tradeMethod,
            String contact
    ) throws SQLException {
        Goods goods = new Goods();
        goods.setUserId(userId);
        ServiceResult<Void> validation = populateAndValidate(
                goods,
                title,
                description,
                priceValue,
                categoryIdValue,
                conditionLevel,
                images,
                tradePlace,
                tradeMethod,
                contact
        );
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }
        return ServiceResult.success("商品发布成功", goodsDao.create(goods));
    }

    public ServiceResult<PostToggleResult> toggleFavorite(
            long goodsId,
            long userId
    ) throws SQLException {
        if (goodsId <= 0) {
            return ServiceResult.failure("商品参数无效");
        }
        PostToggleResult result = goodsDao.toggleFavorite(goodsId, userId);
        return ServiceResult.success(
                result.active() ? "收藏成功" : "已取消收藏",
                result
        );
    }

    public ServiceResult<Goods> update(
            long goodsId,
            long userId,
            boolean admin,
            String title,
            String description,
            String priceValue,
            String categoryIdValue,
            String conditionLevel,
            String images,
            String tradePlace,
            String tradeMethod,
            String contact
    ) throws SQLException {
        if (goodsId <= 0) {
            return ServiceResult.failure("商品参数无效");
        }
        Goods goods = new Goods();
        goods.setId(goodsId);
        goods.setUserId(userId);
        ServiceResult<Void> validation = populateAndValidate(
                goods,
                title,
                description,
                priceValue,
                categoryIdValue,
                conditionLevel,
                images,
                tradePlace,
                tradeMethod,
                contact
        );
        if (!validation.success()) {
            return ServiceResult.failure(validation.message());
        }
        return goodsDao.update(goods, admin)
                .map(value -> ServiceResult.success("商品已更新", value))
                .orElseGet(() -> ServiceResult.failure("商品不存在或无权编辑"));
    }

    public ServiceResult<Void> updateStatus(
            long goodsId,
            long userId,
            boolean admin,
            String status
    ) throws SQLException {
        if (goodsId <= 0 || !STATUSES.contains(status)) {
            return ServiceResult.failure("商品状态参数无效");
        }
        if (!goodsDao.updateStatus(goodsId, userId, admin, status)) {
            return ServiceResult.failure(
                    admin
                            ? "商品不存在或无权修改"
                            : "商品不存在、无权修改或已售出"
            );
        }
        return ServiceResult.success(
                "off_shelf".equals(status) ? "商品已下架" : "商品状态已更新",
                null
        );
    }

    public String normalizeKeyword(String keyword) {
        keyword = ValidationUtils.trimToNull(keyword);
        if (keyword == null) {
            return null;
        }
        return keyword.length() <= 100 ? keyword : keyword.substring(0, 100);
    }

    public String normalizeListStatus(String status) {
        return status != null && LIST_STATUSES.contains(status) ? status : null;
    }

    public String normalizeSort(String sort) {
        return sort != null && SORTS.contains(sort) ? sort : "latest";
    }

    public String normalizeTradeMethodFilter(String tradeMethod) {
        return tradeMethod != null && TRADE_METHODS.contains(tradeMethod)
                ? tradeMethod
                : null;
    }

    public Long normalizeCategoryId(String value) {
        return parseOptionalPositiveLong(value);
    }

    private ServiceResult<Void> populateAndValidate(
            Goods goods,
            String title,
            String description,
            String priceValue,
            String categoryIdValue,
            String conditionLevel,
            String images,
            String tradePlace,
            String tradeMethod,
            String contact
    ) throws SQLException {
        title = ValidationUtils.trimToNull(title);
        description = ValidationUtils.trimToNull(description);
        conditionLevel = ValidationUtils.trimToNull(conditionLevel);
        images = ValidationUtils.trimToNull(images);
        tradePlace = ValidationUtils.trimToNull(tradePlace);
        tradeMethod = ValidationUtils.trimToNull(tradeMethod);
        contact = ValidationUtils.trimToNull(contact);
        Long categoryId = parseOptionalPositiveLong(categoryIdValue);
        BigDecimal price = parsePrice(priceValue);

        if (title == null || title.length() > 150) {
            return ServiceResult.failure("商品标题不能为空且不能超过 150 个字符");
        }
        if (description == null) {
            return ServiceResult.failure("商品描述不能为空");
        }
        if (price == null || price.signum() < 0
                || price.compareTo(MAX_PRICE) > 0) {
            return ServiceResult.failure("请输入有效的商品价格");
        }
        if (categoryId == null || !goodsDao.isActiveGoodsCategory(categoryId)) {
            return ServiceResult.failure("请选择有效的商品分类");
        }
        if (conditionLevel == null || conditionLevel.length() > 50) {
            return ServiceResult.failure("新旧程度不能为空且不能超过 50 个字符");
        }
        if (tradePlace != null && tradePlace.length() > 150) {
            return ServiceResult.failure("交易地点不能超过 150 个字符");
        }
        if (!TRADE_METHODS.contains(tradeMethod)) {
            return ServiceResult.failure("请选择有效的交易方式");
        }
        if (contact == null || contact.length() > 100) {
            return ServiceResult.failure("联系方式不能为空且不能超过 100 个字符");
        }

        goods.setTitle(title);
        goods.setDescription(description);
        goods.setPrice(price);
        goods.setCategoryId(categoryId);
        goods.setConditionLevel(conditionLevel);
        goods.setImages(images);
        goods.setTradePlace(tradePlace);
        goods.setTradeMethod(tradeMethod);
        goods.setContact(contact);
        return ServiceResult.success("验证通过", null);
    }

    private BigDecimal parsePrice(String value) {
        try {
            return new BigDecimal(value).setScale(2);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private Long parseOptionalPositiveLong(String value) {
        if (ValidationUtils.trimToNull(value) == null) {
            return null;
        }
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
