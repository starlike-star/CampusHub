package cn.campushub.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LostFoundDeleteServlet extends LostFoundStatusServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("requestedDelete", true);
        request.getParameterMap();
        HttpServletRequest wrapped = new StatusRequestWrapper(request, "closed");
        super.doPost(wrapped, response);
    }

    private static class StatusRequestWrapper
            extends javax.servlet.http.HttpServletRequestWrapper {
        private final String status;

        StatusRequestWrapper(HttpServletRequest request, String status) {
            super(request);
            this.status = status;
        }

        @Override
        public String getParameter(String name) {
            return "status".equals(name) ? status : super.getParameter(name);
        }
    }
}
