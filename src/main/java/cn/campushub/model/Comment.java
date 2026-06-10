package cn.campushub.model;

import java.time.LocalDateTime;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getAuthorCollege() {
        return authorCollege;
    }

    public void setAuthorCollege(String authorCollege) {
        this.authorCollege = authorCollege;
    }

    public String getAuthorGrade() {
        return authorGrade;
    }

    public void setAuthorGrade(String authorGrade) {
        this.authorGrade = authorGrade;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getAuthorInitial() {
        return authorNickname == null || authorNickname.isBlank()
                ? "U"
                : authorNickname.substring(0, 1);
    }
}
