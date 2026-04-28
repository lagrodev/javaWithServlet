package ru.hexaend.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.hexaend.domain.entity.Contact;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HTTP end-to-end интеграционный тест сервлетов.
 *
 * <p>Проверяет HTTP-эндпоинты через embedded Tomcat + {@link java.net.http.HttpClient}: GET/POST
 * {@code /contacts}, {@code /contacts/form}. Слой: HTTP (сервлеты). Контейнеры: PostgreSQL +
 * embedded Tomcat.
 *
 * @author Vasily Melnik
 */
@DisplayName("ServletEndpoint — HTTP e2e")
class ServletEndpointIT extends AbstractServletIT {

    /**
     * Выполняет GET-запрос и возвращает тело ответа как строку.
     *
     * @param path путь относительно {@link #baseUrl()}
     * @return тело ответа
     */
    private String doGet(String path) throws Exception {
        final HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl() + path))
                        .GET()
                        .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .body();
    }

    /**
     * Выполняет GET-запрос и возвращает полный ответ (статус + тело).
     *
     * @param path путь относительно {@link #baseUrl()}
     * @return HTTP-ответ
     */
    private HttpResponse<String> doGetFull(String path) throws Exception {
        final HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl() + path))
                        .GET()
                        .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    /**
     * Выполняет POST-запрос с URL-encoded телом и возвращает полный ответ.
     *
     * @param path        путь относительно {@link #baseUrl()}
     * @param formBody    URL-encoded тело запроса
     * @return HTTP-ответ
     */
    private HttpResponse<String> doPost(String path, String formBody) throws Exception {
        final HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl() + path))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(formBody))
                        .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    // --- GET /contacts ---

    @Nested
    @DisplayName("GET /contacts")
    class GetContacts {

        /**
         * Пустой справочник → 200, HTML без контактов.
         *
         * <p>Проверяет, что GET /contacts возвращает статус 200 и HTML-страницу без записей.
         */
        @Test
        @DisplayName("пустой справочник → 200, HTML без контактов")
        void emptyPhoneBook_returns200EmptyHtml() throws Exception {
            final HttpResponse<String> response = doGetFull("/contacts");

            assertEquals(200, response.statusCode());
            // Страница отображается, но без строк контактов
            assertFalse(response.body().contains("Smith"));
        }

        /**
         * После добавления контакта → 200, HTML содержит фамилию.
         *
         * <p>Добавляет контакт через сервис, затем проверяет что GET /contacts содержит фамилию в
         * HTML.
         */
        @Test
        @DisplayName("после добавления контакта → 200, HTML содержит фамилию")
        void afterAddContact_htmlContainsLastName() throws Exception {
            phoneBookService.addContact("John", "Smith", List.of("+1234567890"));

            final HttpResponse<String> response = doGetFull("/contacts");

            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("Smith"), "HTML должен содержать фамилию контакта");
        }
    }

    // --- POST /contacts/form — создание ---

    @Nested
    @DisplayName("POST /contacts/form — создание")
    class PostFormCreate {

        /**
         * Валидные данные → 302 redirect, контакт в БД.
         *
         * <p>Отправляет POST с firstName, lastName, phones, проверяет редирект 302 и наличие контакта
         * в БД через репозиторий.
         */
        @Test
        @DisplayName("валидные данные → 302 redirect, контакт в БД")
        void validData_redirect302_contactInDb() throws Exception {
            final HttpResponse<String> response =
                    doPost(
                            "/contacts/form",
                            "firstName=John&lastName=Smith&phones=%2B1234567890");

            assertEquals(302, response.statusCode(), "Должен быть редирект 302");

            // Контакт сохранён в БД
            final List<Contact> all = contactRepository.findAll();
            assertEquals(1, all.size(), "В БД должен быть один контакт");
            assertEquals("Smith", all.get(0).getLastName());
        }

        /**
         * Happy-path полная цепочка: создать → GET /contacts содержит → удалить → GET не содержит.
         *
         * <p>Создаёт контакт через POST, проверяет что он отображается в GET /contacts, затем
         * удаляет через POST /contacts action=delete, проверяет что контакт исчез из списка.
         */
        @Test
        @DisplayName("создать → GET содержит → удалить → GET не содержит")
        void fullCreateDeleteChain() throws Exception {
            // 1. Создаём контакт через POST /contacts/form
            final HttpResponse<String> createResponse =
                    doPost(
                            "/contacts/form",
                            "firstName=John&lastName=Smith&phones=%2B1234567890");
            assertEquals(302, createResponse.statusCode());

            // 2. GET /contacts содержит фамилию
            final String htmlAfterCreate = doGet("/contacts");
            assertTrue(htmlAfterCreate.contains("Smith"), "HTML должен содержать Smith");

            // 3. Получаем UUID созданного контакта
            final List<Contact> contacts = contactRepository.findAll();
            assertEquals(1, contacts.size());
            final UUID contactId = contacts.get(0).getId();

            // 4. Удаляем через POST /contacts action=delete
            final HttpResponse<String> deleteResponse =
                    doPost(
                            "/contacts",
                            "action=delete&id=" + contactId);
            assertEquals(302, deleteResponse.statusCode());

            // 5. GET /contacts не содержит фамилию
            final String htmlAfterDelete = doGet("/contacts");
            assertFalse(htmlAfterDelete.contains("Smith"), "HTML не должен содержать Smith");
        }
    }

    // --- POST /contacts/form — редактирование ---

    @Nested
    @DisplayName("POST /contacts/form — редактирование")
    class PostFormEdit {

        /**
         * Передаём id + новые поля → 302, данные обновлены в БД.
         *
         * <p>Создаёт контакт, затем отправляет POST /contacts/form с id и новыми данными, проверяет
         * что контакт обновлён в БД.
         */
        @Test
        @DisplayName("id + новые поля → 302, данные обновлены в БД")
        void editWithId_redirect302_dataUpdated() throws Exception {
            // Создаём контакт через сервис
            final Contact contact =
                    phoneBookService.addContact("John", "Smith", List.of("+1234567890"));

            // Редактируем через POST /contacts/form с id
            final HttpResponse<String> response =
                    doPost(
                            "/contacts/form",
                            "id=" + contact.getId()
                                    + "&firstName=James&lastName=Brown&phones=%2B1987654321");

            assertEquals(302, response.statusCode(), "Должен быть редирект 302");

            // Проверяем обновлённые данные в БД
            final Contact updated = contactRepository.findById(contact.getId()).orElseThrow();
            assertEquals("James", updated.getFirstName());
            assertEquals("Brown", updated.getLastName());
            assertEquals(List.of("+1987654321"), updated.getPhoneNumbers());
        }
    }

    // --- POST /contacts — удаление ---

    @Nested
    @DisplayName("POST /contacts — удаление")
    class PostDelete {

        /**
         * action=delete + id → 302, контакт удалён из БД.
         *
         * <p>Создаёт контакт, отправляет POST /contacts с action=delete и id, проверяет редирект и
         * отсутствие контакта в БД.
         */
        @Test
        @DisplayName("action=delete + id → 302, контакт удалён из БД")
        void deleteAction_redirect302_contactDeletedFromDb() throws Exception {
            final Contact contact =
                    phoneBookService.addContact("John", "Smith", List.of("+1234567890"));

            final HttpResponse<String> response =
                    doPost("/contacts", "action=delete&id=" + contact.getId());

            assertEquals(302, response.statusCode(), "Должен быть редирект 302");

            // Контакт удалён из БД
            assertTrue(
                    contactRepository.findById(contact.getId()).isEmpty(),
                    "Контакт должен быть удалён из БД");
        }
    }

    // --- GET /contacts/form ---

    @Nested
    @DisplayName("GET /contacts/form")
    class GetForm {

        /**
         * С id → 200, форма содержит данные контакта.
         *
         * <p>Создаёт контакт, запрашивает GET /contacts/form?id=..., проверяет что форма содержит
         * данные контакта.
         */
        @Test
        @DisplayName("с id → 200, форма содержит данные контакта")
        void withId_returns200_formContainsContactData() throws Exception {
            final Contact contact =
                    phoneBookService.addContact("John", "Smith", List.of("+1234567890"));

            final HttpResponse<String> response =
                    doGetFull("/contacts/form?id=" + contact.getId());

            assertEquals(200, response.statusCode());
            // Форма должна содержать данные контакта
            assertTrue(response.body().contains("John"), "Форма должна содержать имя");
            assertTrue(response.body().contains("Smith"), "Форма должна содержать фамилию");
        }
    }

    // --- Error cases ---

    @Nested
    @DisplayName("Error cases")
    class ErrorCases {

        /**
         * POST /contacts action=delete с несуществующим id → ErrorFilter отдаёт 404.
         *
         * <p>Отправляет запрос на удаление с UUID, которого нет в БД. {@link
         * ru.hexaend.service.impl.PhoneBookServiceImpl} выбрасывает {@link
         * ru.hexaend.domain.exeptions.ContactNotFoundException}, который перехватывается {@link
         * ru.hexaend.domain.exeptions.ErrorFilter} и отдаёт 404.
         */
        @Test
        @DisplayName("action=delete с несуществующим id → 404")
        void deleteNonExistentId_returns404() throws Exception {
            final UUID nonExistentId = UUID.randomUUID();

            final HttpResponse<String> response =
                    doPost("/contacts", "action=delete&id=" + nonExistentId);

            assertEquals(404, response.statusCode(), "Должен быть статус 404");
        }
    }
}
