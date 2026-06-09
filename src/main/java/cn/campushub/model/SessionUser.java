package cn.campushub.model;

import java.io.Serial;
import java.io.Serializable;

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
        return new SessionUser(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
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
