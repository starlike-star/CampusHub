package cn.campushub.util;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static void write(HttpServletResponse response, int status, Object value)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(toJson(value));
    }

    public static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String string) {
            return quote(string);
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder builder = new StringBuilder("{");
            Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> entry = iterator.next();
                builder.append(quote(String.valueOf(entry.getKey())))
                        .append(':')
                        .append(toJson(entry.getValue()));
                if (iterator.hasNext()) {
                    builder.append(',');
                }
            }
            return builder.append('}').toString();
        }
        if (value instanceof Collection<?> collection) {
            StringBuilder builder = new StringBuilder("[");
            Iterator<?> iterator = collection.iterator();
            while (iterator.hasNext()) {
                builder.append(toJson(iterator.next()));
                if (iterator.hasNext()) {
                    builder.append(',');
                }
            }
            return builder.append(']').toString();
        }
        throw new IllegalArgumentException("不支持的 JSON 类型：" + value.getClass().getName());
    }

    private static String quote(String value) {
        StringBuilder builder = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> {
                    if (character < 0x20) {
                        builder.append(String.format("\\u%04x", (int) character));
                    } else {
                        builder.append(character);
                    }
                }
            }
        }
        return builder.append('"').toString();
    }
}
