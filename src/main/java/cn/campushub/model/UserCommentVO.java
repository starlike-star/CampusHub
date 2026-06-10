package cn.campushub.model;

import java.time.LocalDateTime;

public record UserCommentVO(
        long id,
        long postId,
        String content,
        int likeCount,
        LocalDateTime createdAt,
        String postTitle
) {
}
