package cn.campushub.constant;

/**
 * 集中定义会话属性名等跨模块共享常量，避免散落的字符串字面量。
 */
public final class SessionConstants {
    public static final String LOGIN_USER = "loginUser";
    public static final String REDIRECT_AFTER_LOGIN = "redirectAfterLogin";
    public static final String LOGIN_CAPTCHA = "loginCaptcha";

    /**
     * 初始化`SessionConstants`对象及其运行所需依赖。
     */
    private SessionConstants() {
    }
}
