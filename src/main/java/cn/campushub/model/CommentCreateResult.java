package cn.campushub.model;

/**
 * 封装评论操作的处理结果与返回数据。
 */
public record CommentCreateResult(Comment comment, int commentCount) {
}
