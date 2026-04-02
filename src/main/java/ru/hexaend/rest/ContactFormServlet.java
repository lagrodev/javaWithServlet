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

public class ContactFormServlet extends HttpServlet {

    private PhoneBookService getService() {
        return (PhoneBookService) getServletContext().getAttribute("service");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rawId = request.getParameter("id");
        if (rawId != null && !rawId.isBlank()) {
            Contact contact = getService().getContactById(UUID.fromString(rawId));
            request.setAttribute("contact", contact);
        }
        request.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rawId = request.getParameter("id");

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        List<String> phones = Arrays.stream(request.getParameter("phones").split(","))
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
