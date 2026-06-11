package cn.campushub.util;

import javax.servlet.http.HttpServletRequest;

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
            return stripTrailingSlash(configured);
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
        String host = request.getServerName();
        return "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "::1".equals(host);
    }

    private static String stripTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
