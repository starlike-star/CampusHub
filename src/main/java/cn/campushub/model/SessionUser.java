package cn.campushub.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表示系统中的SessionUser领域数据，并提供对应属性访问。
 */
public record SessionUser(
        long id,
        String username,
        String nickname,
        String avatar,
        String role
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static SessionUser from(User user) {
        String avatar = user.getAvatar();
        boolean defaultAvatar = avatar == null
                || avatar.isBlank()
                || "images/default-avatar.png".equals(avatar)
                || "images/default-user.png".equals(avatar);
        if (defaultAvatar) {
            avatar = "admin".equalsIgnoreCase(user.getRole())
                    ? "images/Admin.png"
                    : "images/default-user.png";
        }
        return new SessionUser(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                avatar,
                user.getRole()
        );
    }

    public String avatarText() {
        if (nickname == null || nickname.isBlank()) {
            return "U";
        }
        return nickname.substring(0, 1);
    }
}
