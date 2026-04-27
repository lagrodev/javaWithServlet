package ru.hexaend.rest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * Servlet-фильтр для установки кодировки UTF-8
 * на все входящие запросы и исходящие ответы.
 *
 * @author Vasily Melnik
 */
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
        // Инициализация не требуется
    }

    @Override
    public void destroy() {
        // Освобождение ресурсов не требуется
    }

    /**
     * Устанавливает кодировку UTF-8 для запроса и ответа.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}
