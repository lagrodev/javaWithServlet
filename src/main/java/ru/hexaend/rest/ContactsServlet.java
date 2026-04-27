package ru.hexaend.rest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hexaend.service.PhoneBookService;

import java.io.IOException;
import java.util.UUID;

/**
 * Сервлет списка контактов.
 *
 * <p>GET — отображает список всех контактов ({@code contacts.jsp}).<br>
 * POST — обрабатывает действия: удаление контакта ({@code action=delete}).</p>
 *
 * @author Vasily Melnik
 */
public class ContactsServlet extends HttpServlet {

    /**
     * @return экземпляр {@link PhoneBookService} из {@code ServletContext}
     */
    private PhoneBookService getService() {
        return (PhoneBookService) getServletContext().getAttribute("service");
    }

    /**
     * Загружает список всех контактов и перенаправляет на страницу отображения.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("contacts", getService().getAllContacts());
        request.getRequestDispatcher("/WEB-INF/views/contacts.jsp").forward(request, response);
    }

    /**
     * Обрабатывает POST-запросы: удаление контакта.
     *
     * <p>Параметры запроса:
     * <ul>
     *   <li>{@code action} — тип действия ({@code "delete"})</li>
     *   <li>{@code id} — UUID контакта</li>
     * </ul></p>
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        final String action = request.getParameter("action");
        final String rawId = request.getParameter("id");
        if (rawId == null || rawId.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/contacts");
            return;
        }
        final UUID id = UUID.fromString(rawId);
        if ("delete".equals(action)) {
            getService().deleteContact(id);
        }
        response.sendRedirect(request.getContextPath() + "/contacts");
    }
}
