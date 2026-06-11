package cn.campushub.util;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * 构造并校验站内交易流程使用的安全跳转地址。
 */
public final class TradeUrlUtils {
    private TradeUrlUtils() {
    }

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

    private static String stripTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
