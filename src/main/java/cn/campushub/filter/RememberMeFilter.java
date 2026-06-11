package cn.campushub.filter;

import cn.campushub.service.RememberMeService;
import cn.campushub.util.SessionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class RememberMeFilter implements Filter {
    private final RememberMeService rememberMeService = new RememberMeService();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!SessionUtils.isLoggedIn(httpRequest)) {
            try {
                rememberMeService.autoLogin(httpRequest, httpResponse)
                        .ifPresent(user -> SessionUtils.login(httpRequest, user));
            } catch (SQLException exception) {
                httpRequest.getServletContext().log(
                        "Remember-me auto login failed",
                        exception
                );
            }
        }
        chain.doFilter(request, response);
    }
}
