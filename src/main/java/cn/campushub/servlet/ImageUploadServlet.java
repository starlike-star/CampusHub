package cn.campushub.servlet;

import cn.campushub.model.SessionUser;
import cn.campushub.util.JsonUtils;
import cn.campushub.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ImageUploadServlet extends HttpServlet {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "avatar", "post", "goods", "lost_found", "activity", "common"
    );
    private static final Map<String, Set<String>> MIME_TYPES = Map.of(
            "jpg", Set.of("image/jpeg", "image/pjpeg"),
            "jpeg", Set.of("image/jpeg", "image/pjpeg"),
            "png", Set.of("image/png"),
            "webp", Set.of("image/webp")
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUser user = SessionUtils.currentUser(request);
        if (user == null) {
            writeError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "请先登录",
                    true
            );
            return;
        }

        String type = normalize(request.getParameter("type"));
        if (!ALLOWED_TYPES.contains(type)) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "图片类型参数无效");
            return;
        }

        Part file;
        try {
            file = request.getPart("file");
        } catch (IllegalStateException exception) {
            writeError(
                    response,
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "图片不能超过 5MB"
            );
            return;
        } catch (ServletException exception) {
            writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "上传请求格式无效"
            );
            return;
        }
        if (file == null || file.getSize() == 0) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "请选择图片");
            return;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            writeError(
                    response,
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "图片不能超过 5MB"
            );
            return;
        }

        String extension = extensionOf(file.getSubmittedFileName());
        if (!MIME_TYPES.containsKey(extension)) {
            writeError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "图片格式不支持");
            return;
        }
        String contentType = normalize(file.getContentType());
        if (!MIME_TYPES.get(extension).contains(contentType)) {
            writeError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "图片 MIME 类型与文件后缀不匹配");
            return;
        }

        String detectedType;
        try (InputStream input = file.getInputStream()) {
            detectedType = detectImageType(input);
        }
        if (!matchesExtension(extension, detectedType)) {
            writeError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "图片内容与文件格式不匹配");
            return;
        }

        String realDirectory = getServletContext().getRealPath("/uploads/" + type);
        if (realDirectory == null || realDirectory.isBlank()) {
            writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "服务器上传目录不可用"
            );
            return;
        }

        Path directory = Path.of(realDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            log("创建上传目录失败", exception);
            writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "服务器上传目录不可用"
            );
            return;
        }
        String storedExtension = "jpeg".equals(extension) ? "jpg" : extension;
        String fileName = UUID.randomUUID() + "." + storedExtension;
        Path destination = directory.resolve(fileName).normalize();
        if (!destination.getParent().equals(directory)) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "文件名无效");
            return;
        }

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            log("保存上传图片失败", exception);
            writeError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "图片保存失败，请稍后重试"
            );
            return;
        }

        JsonUtils.write(
                response,
                HttpServletResponse.SC_OK,
                Map.of(
                        "success", true,
                        "url", "/uploads/" + type + "/" + fileName,
                        "message", "上传成功"
                )
        );
    }

    private String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? normalize(fileName.substring(index + 1)) : "";
    }

    private String detectImageType(InputStream input) throws IOException {
        byte[] header = input.readNBytes(12);
        if (header.length >= 3
                && (header[0] & 0xff) == 0xff
                && (header[1] & 0xff) == 0xd8
                && (header[2] & 0xff) == 0xff) {
            return "jpeg";
        }
        if (header.length >= 8
                && (header[0] & 0xff) == 0x89
                && header[1] == 0x50
                && header[2] == 0x4e
                && header[3] == 0x47
                && header[4] == 0x0d
                && header[5] == 0x0a
                && header[6] == 0x1a
                && header[7] == 0x0a) {
            return "png";
        }
        if (header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P') {
            return "webp";
        }
        return "";
    }

    private boolean matchesExtension(String extension, String detectedType) {
        return ("jpg".equals(extension) || "jpeg".equals(extension))
                ? "jpeg".equals(detectedType)
                : extension.equals(detectedType);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        writeError(response, status, message, false);
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message,
            boolean needLogin
    ) throws IOException {
        JsonUtils.write(
                response,
                status,
                needLogin
                        ? Map.of(
                                "success", false,
                                "needLogin", true,
                                "message", message
                        )
                        : Map.of("success", false, "message", message)
        );
    }
}
