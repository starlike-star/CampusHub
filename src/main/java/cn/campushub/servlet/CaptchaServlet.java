package cn.campushub.servlet;

import cn.campushub.constant.SessionConstants;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 生成图形验证码并将校验值保存到当前会话。
 */
public class CaptchaServlet extends HttpServlet {
    private static final String CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 5;
    private static final int WIDTH = 150;
    private static final int HEIGHT = 48;
    private final SecureRandom random = new SecureRandom();

    /**
     * 处理`Captcha`相关的 HTTP GET 请求并生成响应。
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws IOException 读取请求或写入响应失败时抛出
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String code = generateCode();
        request.getSession(true).setAttribute(SessionConstants.LOGIN_CAPTCHA, code);

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        BufferedImage image =
                new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            render(graphics, code);
        } finally {
            graphics.dispose();
        }
        ImageIO.write(image, "png", response.getOutputStream());
    }

    /**
     * 生成`Code`。
     *
     * @return `generateCode`
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    /**
     * 处理 `render` 对应的业务流程。
     *
     * @param graphics 参数 `graphics`
     * @param code 参数 `code`
     */
    private void render(Graphics2D graphics, String code) {
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON //k
        );
        graphics.setColor(new Color(241, 245, 249)); //浅
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        for (int index = 0; index < 7; index++) {
            graphics.setColor(randomColor(145, 210)); //g7
            graphics.drawLine(
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT),
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT)
            );
        }

        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28)); //p
        for (int index = 0; index < code.length(); index++) {
            graphics.setColor(randomColor(25, 125));
            int x = 13 + index * 27;
            int y = 34 + random.nextInt(7) - 3;
            graphics.drawString(String.valueOf(code.charAt(index)), x, y);
        }

        for (int index = 0; index < 45; index++) {//z
            graphics.setColor(randomColor(120, 220));
            graphics.fillRect(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }
    }

    /**
     * 根据输入计算并返回 `randomColor` 的处理结果。
     *
     * @param minimum 参数 `minimum`
     * @param maximum 参数 `maximum`
     * @return 方法处理结果
     */
    private Color randomColor(int minimum, int maximum) {
        int range = maximum - minimum;
        return new Color(
                minimum + random.nextInt(range),
                minimum + random.nextInt(range),
                minimum + random.nextInt(range)
        );
    }
}
