package cn.campushub.servlet;

/**
 * Compatibility alias for older deployments. New mappings use PostCommentServlet.
 */
@Deprecated
/**
 * 接收评论的请求处理请求，调用业务层并生成 HTTP 响应。
 */
public class CommentServlet extends PostCommentServlet {
}
