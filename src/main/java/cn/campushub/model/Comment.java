package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 表示系统中的评论领域数据，并提供对应属性访问。
 */
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createdAt;
    private String authorAvatar;
    private String authorNickname;
    private String authorCollege;
    private String authorGrade;
    private boolean liked;

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
     * 获取帖子编号。
     *
     * @return 帖子编号
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * 设置帖子编号。
     *
     * @param postId 帖子编号
     */
    public void setPostId(Long postId) {
        this.postId = postId;
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
