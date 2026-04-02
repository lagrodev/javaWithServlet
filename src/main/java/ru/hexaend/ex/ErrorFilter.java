package ru.hexaend.ex;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (ApplicationException e) {
            handleError(req, resp, e.getStatus(), e.getMessage());
        } catch (RuntimeException e) {
            handleError(req, resp, 500, e.getMessage());
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp,
                             int status, String message) throws ServletException, IOException {
        req.setAttribute("error", new AppError("ERROR", message, status));
        resp.setStatus(status);
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }

    @Override public void init(FilterConfig config) {}
    @Override public void destroy() {}
}