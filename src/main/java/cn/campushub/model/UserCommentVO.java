package cn.campushub.model;

import java.time.LocalDateTime;

/**
 * 聚合用户页面展示所需的数据。
 */
public record UserCommentVO(
        long id,
        long postId,
        String content,
        int likeCount,
        LocalDateTime createdAt,
        String postTitle
) {
}
