package cn.campushub.servlet;

import cn.campushub.util.UploadStorage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Serves files from the external upload directory.
 */
public class UploadedFileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Path file = UploadStorage.resolvePublicPath(request.getPathInfo());
        if (file == null || !Files.isRegularFile(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(contentType(file));
        response.setHeader("Cache-Control", "public, max-age=604800");
        response.setContentLengthLong(Files.size(file));
        try (OutputStream output = response.getOutputStream()) {
            Files.copy(file, output);
        }
    }

    private String contentType(Path file) throws IOException {
        String detected = Files.probeContentType(file);
        if (detected != null && detected.startsWith("image/")) {
            return detected;
        }
        String fileName = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }
}
