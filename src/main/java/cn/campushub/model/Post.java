package cn.campushub.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 表示系统中的帖子领域数据，并提供对应属性访问。
 */
public class Post {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
    private String images;
    private String topic;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorAvatar;
    private String authorNickname;
    private String authorCollege;
    private String authorGrade;
    private String categoryName;
    private boolean liked;
    private boolean favorited;

    /**
     * 获取编号。
     *
     * @return 编号
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置编号。
     *
     * @param id 业务数据编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户编号。
     *
     * @return 用户编号
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户编号。
     *
     * @param userId 用户编号
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取分类编号。
     *
     * @return 分类编号
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置分类编号。
     *
     * @param categoryId 分类编号
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取标题。
     *
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题。
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取`Content`。
     *
     * @return `Content`
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置`Content`。
     *
     * @param content 正文内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取`Images`。
     *
     * @return `Images`
     */
    public String getImages() {
        return images;
    }

    /**
     * 设置`Images`。
     *
     * @param images 参数 `images`
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * 获取`Topic`。
     *
     * @return `Topic`
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置`Topic`。
     *
     * @param topic 参数 `topic`
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取点赞数量。
     *
     * @return 点赞数量
     */
    public Integer getLikeCount() {
        return likeCount;
    }

    /**
     * 设置点赞数量。
     *
     * @param likeCount 参数 `likeCount`
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 获取评论数量。
     *
     * @return 评论数量
     */
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     * 设置评论数量。
     *
     * @param commentCount 参数 `commentCount`
     */
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * 获取收藏数量。
     *
     * @return 收藏数量
     */
    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * 设置收藏数量。
     *
     * @param favoriteCount 参数 `favoriteCount`
     */
    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * 获取`ViewCount`。
     *
     * @return `ViewCount`
     */
    public Integer getViewCount() {
        return viewCount;
    }

    /**
     * 设置`ViewCount`。
     *
     * @param viewCount 参数 `viewCount`
     */
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 业务状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取`CreatedAt`。
     *
     * @return `CreatedAt`
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置`CreatedAt`。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取`UpdatedAt`。
     *
     * @return `UpdatedAt`
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置`UpdatedAt`。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取`AuthorAvatar`。
     *
     * @return `AuthorAvatar`
     */
    public String getAuthorAvatar() {
        return authorAvatar;
    }

    /**
     * 设置`AuthorAvatar`。
     *
     * @param authorAvatar 参数 `authorAvatar`
     */
    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    /**
     * 获取`AuthorNickname`。
     *
     * @return `AuthorNickname`
     */
    public String getAuthorNickname() {
        return authorNickname;
    }

    /**
     * 设置`AuthorNickname`。
     *
     * @param authorNickname 参数 `authorNickname`
     */
    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    /**
     * 获取`AuthorCollege`。
     *
     * @return `AuthorCollege`
     */
    public String getAuthorCollege() {
        return authorCollege;
    }

    /**
     * 设置`AuthorCollege`。
     *
     * @param authorCollege 参数 `authorCollege`
     */
    public void setAuthorCollege(String authorCollege) {
        this.authorCollege = authorCollege;
    }

    /**
     * 获取`AuthorGrade`。
     *
     * @return `AuthorGrade`
     */
    public String getAuthorGrade() {
        return authorGrade;
    }

    /**
     * 设置`AuthorGrade`。
     *
     * @param authorGrade 参数 `authorGrade`
     */
    public void setAuthorGrade(String authorGrade) {
        this.authorGrade = authorGrade;
    }

    /**
     * 获取`CategoryName`。
     *
     * @return `CategoryName`
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置`CategoryName`。
     *
     * @param categoryName 参数 `categoryName`
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 判断是否`Liked`。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean isLiked() {
        return liked;
    }

    /**
     * 设置`Liked`。
     *
     * @param liked 是否已点赞
     */
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    /**
     * 判断是否`Favorited`。
     *
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public boolean isFavorited() {
        return favorited;
    }

    /**
     * 设置`Favorited`。
     *
     * @param favorited 参数 `favorited`
     */
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    /**
     * 获取`Summary`。
     *
     * @return `Summary`
     */
    public String getSummary() {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 180 ? normalized : normalized.substring(0, 180) + "...";
    }

    /**
     * 获取图片列表。
     *
     * @return 符合条件的数据列表
     */
    public List<String> getImageList() {
        if (images == null || images.isBlank()) {
            return List.of();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    /**
     * 获取`AuthorInitial`。
     *
     * @return `AuthorInitial`
     */
    public String getAuthorInitial() {
        return authorNickname == null || authorNickname.isBlank()
                ? "U"
                : authorNickname.substring(0, 1);
    }
}
