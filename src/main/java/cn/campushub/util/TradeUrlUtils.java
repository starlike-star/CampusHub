package cn.campushub.util;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * 构造并校验站内交易流程使用的安全跳转地址。
 */
public final class TradeUrlUtils {
    /**
     * 初始化交易地址对象及其运行所需依赖。
     */
    private TradeUrlUtils() {
    }

    /**
     * 根据输入计算并返回 `publicBaseUrl` 的处理结果。
     *
     * @param request HTTP 请求对象
     * @return 方法处理结果
     */
    public static String publicBaseUrl(HttpServletRequest request) {
        String configured = ValidationUtils.trimToNull(
                System.getenv("CAMPUSHUB_PUBLIC_BASE_URL")
        );
        if (configured == null) {
            configured = ValidationUtils.trimToNull(
                    System.getProperty("campushub.publicBaseUrl")
            );
        }
        if (configured != null) {
            String normalized = normalizeConfiguredBaseUrl(
                    configured,
                    request.getContextPath()
            );
            if (normalized != null) {
                return normalized;
            }
        }

        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        return scheme + "://" + host + (defaultPort ? "" : ":" + port)
                + request.getContextPath();
    }

    /**
     * 判断是否`Localhost`。
     *
     * @param request HTTP 请求对象
     * @return 满足条件或操作成功时返回 true，否则返回 false
     */
    public static boolean isLocalhost(HttpServletRequest request) {
        String host;
        try {
            host = URI.create(publicBaseUrl(request)).getHost();
        } catch (IllegalArgumentException exception) {
            host = request.getServerName();
        }
        return "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "::1".equals(host)
                || "0:0:0:0:0:0:0:1".equals(host);
    }

    /**
     * 规范化`ConfiguredBaseUrl`。
     *
     * @param configured 参数 `configured`
     * @param contextPath 参数 `contextPath`
     * @return 方法处理结果
     */
    static String normalizeConfiguredBaseUrl(
            String configured,
            String contextPath
    ) {
        String value = stripTrailingSlash(configured);
        try {
            URI uri = URI.create(value);
            if (!uri.isAbsolute() || uri.getHost() == null
                    || uri.getQuery() != null || uri.getFragment() != null) {
                return null;
            }
            String path = uri.getPath();
            if (path != null && !path.isBlank() && !"/".equals(path)) {
                return value;
            }
            String normalizedContextPath =
                    ValidationUtils.trimToNull(contextPath);
            return normalizedContextPath == null || "/".equals(normalizedContextPath)
                    ? value
                    : value + (normalizedContextPath.startsWith("/")
                    ? normalizedContextPath
                    : "/" + normalizedContextPath);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * 根据输入计算并返回 `stripTrailingSlash` 的处理结果。
     *
     * @param value 待处理的值
     * @return 方法处理结果
     */
    private static String stripTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
