package cn.campushub.util;

/**
 * 提供 HTML 特殊字符转义，降低页面输出中的注入风险。
 */
public final class HtmlUtils {
    private static final String DEFAULT_IMAGE_VERSION = "20260611";

    private HtmlUtils() {
    }

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
