package cn.campushub.util;

/**
 * 提供 HTML 特殊字符转义，降低页面输出中的注入风险。
 */
public final class HtmlUtils {
    private static final String DEFAULT_IMAGE_VERSION = "20260611";

    /**
     * 初始化`Html`对象及其运行所需依赖。
     */
    private HtmlUtils() {
    }

    /**
     * 转义`Html`。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 根据输入计算并返回 `resourcePath` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    public static String resourcePath(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String path = value.startsWith("/") ? value : "/" + value;
        if (isDefaultImage(path) && !path.contains("?")) {
            return path + "?v=" + DEFAULT_IMAGE_VERSION;
        }
        return path;
    }

    /**
     * 判断是否`DefaultImage`。
     *
     * @param path 资源路径
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    private static boolean isDefaultImage(String path) {
        return switch (path) {
            case "/images/default-activity.png",
                 "/images/default-goods.png",
                 "/images/default-lostfound.png",
                 "/images/default-user.png",
                 "/images/Admin.png" -> true;
            default -> false;
        };
    }
}
