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

/**
 * 编排商品业务规则、参数校验与数据访问操作。
 */
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

    /**
     * 初始化商品对象及其运行所需依赖。
     */
    public GoodsService() {
        this(new JdbcGoodsDao());
    }

    GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    /**
     * 查询商品。
     *
     * @param currentUserId 当前用户编号
     * @param keyword 搜索关键字
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param status 业务状态
     * @param tradeMethod 参数 `tradeMethod`
     * @param sort 排序方式
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 查询`OwnGoods`。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Goods> listOwnGoods(long userId) throws SQLException {
        return goodsDao.findOwnGoods(userId);
    }

    /**
     * 查询收藏商品。
     *
     * @param userId 用户编号
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Goods> listFavoriteGoods(long userId) throws SQLException {
        return goodsDao.findFavoriteGoods(userId);
    }

    /**
     * 查询`Categories`。
     *
     * @return 符合条件的数据列表
     * @throws SQLException 数据库访问失败时抛出
     */
    public List<Category> listCategories() throws SQLException {
        return goodsDao.findActiveGoodsCategories();
    }

    /**
     * 查询商品详情。
     *
     * @param goodsId 商品编号
     * @param currentUserId 当前用户编号
     * @return 查询到的数据；不存在时返回空结果
     * @throws SQLException 数据库访问失败时抛出
     */
    public Optional<Goods> detail(long goodsId, Long currentUserId)
            throws SQLException {
        if (goodsId <= 0) {
            return Optional.empty();
        }
        return goodsDao.findVisibleById(goodsId, currentUserId);
    }

    /**
     * 创建商品。
     *
     * @param userId 用户编号
     * @param title 标题
     * @param description 描述内容
     * @param priceValue 参数 `priceValue`
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param conditionLevel 参数 `conditionLevel`
     * @param images 参数 `images`
     * @param tradePlace 参数 `tradePlace`
     * @param tradeMethod 参数 `tradeMethod`
     * @param contact 参数 `contact`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 切换收藏。
     *
     * @param goodsId 商品编号
     * @param userId 用户编号
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 更新商品。
     *
     * @param goodsId 商品编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param title 标题
     * @param description 描述内容
     * @param priceValue 参数 `priceValue`
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param conditionLevel 参数 `conditionLevel`
     * @param images 参数 `images`
     * @param tradePlace 参数 `tradePlace`
     * @param tradeMethod 参数 `tradeMethod`
     * @param contact 参数 `contact`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 更新状态。
     *
     * @param goodsId 商品编号
     * @param userId 用户编号
     * @param admin 是否具有管理员权限
     * @param status 业务状态
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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
     * 规范化列表状态。
     *
     * @param status 业务状态
     * @return 方法处理结果
     */
    public String normalizeListStatus(String status) {
        return status != null && LIST_STATUSES.contains(status) ? status : null;
    }

    /**
     * 规范化排序方式。
     *
     * @param sort 排序方式
     * @return 方法处理结果
     */
    public String normalizeSort(String sort) {
        return sort != null && SORTS.contains(sort) ? sort : "latest";
    }

    /**
     * 规范化`TradeMethodFilter`。
     *
     * @param tradeMethod 参数 `tradeMethod`
     * @return 方法处理结果
     */
    public String normalizeTradeMethodFilter(String tradeMethod) {
        return tradeMethod != null && TRADE_METHODS.contains(tradeMethod)
                ? tradeMethod
                : null;
    }

    /**
     * 规范化分类编号。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public Long normalizeCategoryId(String value) {
        return parseOptionalPositiveLong(value);
    }

    /**
     * 填充并校验商品数据。
     *
     * @param goods 商品数据
     * @param title 标题
     * @param description 描述内容
     * @param priceValue 参数 `priceValue`
     * @param categoryIdValue 参数 `categoryIdValue`
     * @param conditionLevel 参数 `conditionLevel`
     * @param images 参数 `images`
     * @param tradePlace 参数 `tradePlace`
     * @param tradeMethod 参数 `tradeMethod`
     * @param contact 参数 `contact`
     * @return 包含处理状态、提示信息和业务数据的结果
     * @throws SQLException 数据库访问失败时抛出
     */
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

    /**
     * 解析`Price`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
    private BigDecimal parsePrice(String value) {
        try {
            return new BigDecimal(value).setScale(2);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /**
     * 解析`OptionalPositiveLong`。
     *
     * @param value 待处理的值
     * @return 解析后的值；输入无效时返回 null
     */
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
