package cn.campushub.util;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/**
 * Resolves the external directory used for user uploaded files.
 */
public final class UploadStorage {
    public static final String URL_PREFIX = "/uploads";
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "avatar", "post", "goods", "lost_found", "activity", "common"
    );

    private UploadStorage() {
    }

    public static Path baseDirectory() {
        String configured = firstNonBlank(
                System.getProperty("campushub.upload.dir"),
                System.getenv("CAMPUSHUB_UPLOAD_DIR")
        );
        if (configured == null) {
            configured = Path.of(
                    System.getProperty("user.home"),
                    "CampusHub",
                    "uploads"
            ).toString();
        }
        return Path.of(configured).toAbsolutePath().normalize();
    }

    public static boolean isAllowedType(String type) {
        return ALLOWED_TYPES.contains(normalize(type));
    }

    public static Path typeDirectory(String type) {
        String normalizedType = normalize(type);
        if (!isAllowedType(normalizedType)) {
            throw new IllegalArgumentException("Unsupported upload type");
        }
        return baseDirectory().resolve(normalizedType).normalize();
    }

    public static String publicUrl(String type, String fileName) {
        return URL_PREFIX + "/" + normalize(type) + "/" + fileName;
    }

    public static Path resolvePublicPath(String pathInfo) {
        if (pathInfo == null || pathInfo.isBlank() || "/".equals(pathInfo)) {
            return null;
        }
        String cleanPath = pathInfo.startsWith("/")
                ? pathInfo.substring(1)
                : pathInfo;
        String[] parts = cleanPath.split("/");
        if (parts.length != 2 || !isAllowedType(parts[0]) || parts[1].isBlank()) {
            return null;
        }
        Path base = baseDirectory();
        Path resolved = base.resolve(parts[0]).resolve(parts[1]).normalize();
        return resolved.startsWith(base) ? resolved : null;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
