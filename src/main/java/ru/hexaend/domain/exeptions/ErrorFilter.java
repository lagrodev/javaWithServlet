package ru.hexaend.domain.exeptions;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hexaend.domain.dto.HttpError;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet-фильтр глобальной обработки ошибок.
 *
 * <p>Перехватывает все {@link ApplicationException} (бизнес-ошибки)
 * и непредвиденные {@link RuntimeException}, логирует их
 * и перенаправляет на страницу ошибки {@code /WEB-INF/views/error.jsp}
 * с соответствующим HTTP-статусом.</p>
 *
 * @author Vasily Melnik
 */
public class ErrorFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(ErrorFilter.class.getName());

    /**
     * Перехватывает исключения из цепочки фильтров/сервлетов.
     * 1) {@link ApplicationException} — устанавливает статус из исключения.
     * 2) Прочие {@link RuntimeException} — статус 500 (Internal Server Error).
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (ApplicationException e) {
            LOG.log(Level.WARNING, "Бизнес-ошибка [{0}]: {1}",
                    new Object[]{e.getHttpError(), e.getMessage()});
            handleError(req, resp, e.getHttpError(), e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Непредвиденная ошибка: " + e.getMessage(), e);
            handleError(req, resp, HttpError.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * Формирует {@link AppError}, устанавливает HTTP-статус
     * и перенаправляет на страницу ошибки.
     */
    private void handleError(HttpServletRequest req, HttpServletResponse resp,
                             HttpError httpError, String message) throws ServletException, IOException {
        req.setAttribute("error", new AppError(httpError, message));
        resp.setStatus(httpError.statusCode());
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }

    @Override
    public void init(FilterConfig config) {
        // Инициализация не требуется
    }

    @Override
    public void destroy() {
        // Освобождение ресурсов не требуется
    }
}