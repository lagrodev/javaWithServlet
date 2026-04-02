package ru.hexaend.rest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hexaend.service.PhoneBookService;

import java.io.IOException;
import java.util.UUID;

public class ContactsServlet extends HttpServlet {

    private PhoneBookService getService() {
        return (PhoneBookService) getServletContext().getAttribute("service");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("contacts", getService().getAllContacts());
        request.getRequestDispatcher("/WEB-INF/views/contacts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String rawId = request.getParameter("id");
        if (rawId == null || rawId.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/contacts");
            return;
        }
        UUID id = UUID.fromString(rawId);
        if ("delete".equals(action)) {
            getService().deleteContact(id);
        }
        response.sendRedirect(request.getContextPath() + "/contacts");
    }
}
