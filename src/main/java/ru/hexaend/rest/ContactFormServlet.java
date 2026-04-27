package ru.hexaend.rest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hexaend.entity.Contact;
import ru.hexaend.service.PhoneBookService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Сервлет формы создания/редактирования контакта.
 *
 * <p>GET — отображает пустую форму (создание) или заполненную (редактирование, если передан {@code id}).<br>
 * POST — сохраняет данные формы: создаёт новый контакт или обновляет существующий.</p>
 *
 * @author Vasily Melnik
 */
public class ContactFormServlet extends HttpServlet {

    /**
     * @return экземпляр {@link PhoneBookService} из {@code ServletContext}
     */
    private PhoneBookService getService() {
        return (PhoneBookService) getServletContext().getAttribute("service");
    }

    /**
     * Отображает форму. Если параметр {@code id} передан — загружает контакт для редактирования.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String rawId = request.getParameter("id");
        if (rawId != null && !rawId.isBlank()) {
            final Contact contact = getService().getContactById(UUID.fromString(rawId));
            request.setAttribute("contact", contact);
        }
        request.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(request, response);
    }

    /**
     * Обрабатывает отправку формы: создание или обновление контакта.
     *
     * <p>Параметры: {@code id} (опц.), {@code firstName}, {@code lastName},
     * {@code phones} (через запятую). Если {@code id} передан — обновление,
     * иначе — создание нового контакта.</p>
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        final String rawId = request.getParameter("id");

        final String firstName = request.getParameter("firstName");
        final String lastName = request.getParameter("lastName");
        final List<String> phones = Arrays.stream(request.getParameter("phones").split(","))
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .toList();

        if (rawId != null && !rawId.isBlank()) {
            getService().editContact(
                    UUID.fromString(rawId),
                    firstName,
                    lastName,
                    phones
            );
            response.sendRedirect("/contacts");
        } else {
            getService().addContact(
                    firstName,
                    lastName,
                    phones
            );
            response.sendRedirect("/contacts");
        }

    }
}
