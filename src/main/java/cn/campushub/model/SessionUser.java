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

    /**
     * 根据输入创建会话用户。
     *
     * @param user 用户数据
     * @return 方法处理结果
     */
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

    /**
     * 获取`avatarText`。
     *
     * @return `avatarText`
     */
    public String avatarText() {
        if (nickname == null || nickname.isBlank()) {
            return "U";
        }
        return nickname.substring(0, 1);
    }
}
