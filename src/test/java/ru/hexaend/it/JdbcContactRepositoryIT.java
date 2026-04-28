package ru.hexaend.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.hexaend.domain.entity.Contact;
import ru.hexaend.repository.impl.JdbcContactRepository;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционный тест для {@link JdbcContactRepository}.
 *
 * <p>Проверяет полноту CRUD-операций и поисковых запросов к реальной PostgreSQL через
 * Testcontainers. Каждый тест работает с чистой БД — очистка выполняется в {@link
 * AbstractDatabaseIT#clearDatabase()}. Слой: репозиторий. Контейнер: PostgreSQL.
 *
 * @author Vasily Melnik
 */
@DisplayName("JdbcContactRepository — интеграционный тест")
class JdbcContactRepositoryIT extends AbstractDatabaseIT {

    private JdbcContactRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcContactRepository(dataSourceProvider);
    }

    /**
     * Вставляет 2 тестовых контакта из SQL-скрипта.
     */
    private void insertTwoContacts() throws Exception {
        try (final Connection conn = dataSourceProvider.getConnection();
             final Statement stmt = conn.createStatement()) {
            stmt.execute(
                    """
                            INSERT INTO contacts (id, first_name, last_name, created_at, updated_at)
                            VALUES
                                ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'John', 'Smith', now(), now()),
                                ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Robert', 'Johnson', now(), now())
                            """);
            stmt.execute(
                    """
                            INSERT INTO phone_numbers (contact_id, phone)
                            VALUES
                                ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '+1234567890'),
                                ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '+1987654321'),
                                ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '+1111222333')
                            """);
        }
    }

    // --- save ---

    @Nested
    @DisplayName("save")
    class Save {

        /**
         * Сохранение нового контакта и проверка через {@code findById}.
         *
         * <p>Создаёт контакт с одним номером, сохраняет, затем находит по UUID и проверяет все поля.
         */
        @Test
        @DisplayName("сохранение нового контакта → findById возвращает его")
        void newContact_findByIdReturnsIt() {
            final Contact contact = new Contact("John", "Smith", List.of("+1234567890"));
            repository.save(contact);

            final Optional<Contact> found = repository.findById(contact.getId());

            assertTrue(found.isPresent(), "Контакт должен быть найден");
            assertEquals("John", found.get().getFirstName());
            assertEquals("Smith", found.get().getLastName());
            assertEquals(List.of("+1234567890"), found.get().getPhoneNumbers());
        }

        /**
         * UPSERT идемпотентность: два вызова {@code save} с одним UUID — в БД один контакт.
         *
         * <p>Сохраняет контакт дважды с одинаковым UUID, затем проверяет что {@code findAll}
         * возвращает ровно один контакт.
         */
        @Test
        @DisplayName("UPSERT идемпотентность: save дважды — один контакт в БД")
        void saveTwice_singleContactInDb() {
            final UUID id = UUID.randomUUID();
            final Contact first = new Contact(id, "John", "Smith", List.of("+1234567890"));
            final Contact second = new Contact(id, "John", "Smith", List.of("+1234567890"));

            repository.save(first);
            repository.save(second);

            final List<Contact> all = repository.findAll();
            assertEquals(1, all.size(), "В БД должен быть ровно один контакт");
        }

        /**
         * Сохранение обновлённого контакта — изменённые поля записываются в БД.
         *
         * <p>Сохраняет контакт, затем изменяет имя, фамилию и телефоны, сохраняет повторно, проверяет
         * что обновлённые поля читаются из БД.
         */
        @Test
        @DisplayName("сохранение обновлённого контакта (изменённые поля пишутся)")
        void updatedContact_changedFieldsPersisted() {
            final Contact contact = new Contact("John", "Smith", List.of("+1234567890"));
            repository.save(contact);

            // Обновляем поля и сохраняем повторно (UPSERT)
            contact.setFirstName("James");
            contact.setLastName("Brown");
            contact.setPhoneNumbers(List.of("+9999999999"));
            repository.save(contact);

            final Optional<Contact> found = repository.findById(contact.getId());
            assertTrue(found.isPresent());
            assertEquals("James", found.get().getFirstName());
            assertEquals("Brown", found.get().getLastName());
            assertEquals(List.of("+9999999999"), found.get().getPhoneNumbers());
        }
    }

    // --- delete ---

    @Nested
    @DisplayName("delete")
    class Delete {

        /**
         * Удаление контакта по UUID — {@code findById} возвращает пустой Optional.
         *
         * <p>Сохраняет контакт, удаляет по ID, проверяет что контакт не находится.
         */
        @Test
        @DisplayName("deleteById → контакт не находится")
        void deleteById_contactNotFound() {
            final Contact contact = new Contact("John", "Smith", List.of("+1234567890"));
            repository.save(contact);

            repository.deleteById(contact.getId());

            final Optional<Contact> found = repository.findById(contact.getId());
            assertTrue(found.isEmpty(), "Контакт не должен быть найден после удаления");
        }

        /**
         * Каскадное удаление: телефоны удаляются вместе с контактом.
         *
         * <p>Сохраняет контакт с телефонами, удаляет контакт, затем проверяет что телефонные номера
         * не остаются в БД (через {@code findByPhoneNumber}).
         */
        @Test
        @DisplayName("каскадное удаление: телефоны удалены вместе с контактом")
        void deleteById_cascadesPhones() {
            final Contact contact = new Contact("John", "Smith", List.of("+1234567890"));
            repository.save(contact);

            repository.deleteById(contact.getId());

            // Телефон удалённого контакта не должен находиться
            final List<Contact> byPhone = repository.findByPhoneNumber("+1234567890");
            assertTrue(byPhone.isEmpty(), "Телефоны должны быть удалены каскадно");
        }
    }

    // --- findAll ---

    @Nested
    @DisplayName("findAll")
    class FindAll {

        /**
         * Пустая таблица возвращает пустой список.
         *
         * <p>После очистки БД (по умолчанию) таблица пуста — проверяем что {@code findAll} возвращает
         * пустой список.
         */
        @Test
        @DisplayName("пустая таблица → пустой список")
        void emptyTable_returnsEmptyList() {
            final List<Contact> all = repository.findAll();

            assertTrue(all.isEmpty(), "Пустая таблица должна возвращать пустой список");
        }

        /**
         * Два контакта — {@code findAll} возвращает оба, правильный порядок.
         *
         * <p>Использует SQL-скрипт для вставки Smith и Johnson. Порядок: Johnson перед Smith
         * (сортировка по фамилии).
         */
        @Test
        @DisplayName("2 контакта → возвращает оба, правильный порядок")
        void twoContacts_returnsBothInOrder() throws Exception {
            insertTwoContacts();

            final List<Contact> all = repository.findAll();

            assertEquals(2, all.size(), "Должно быть 2 контакта");
            // Порядок по фамилии: Johnson (J) перед Smith (S)
            assertEquals("Johnson", all.get(0).getLastName());
            assertEquals("Smith", all.get(1).getLastName());
        }
    }

    // --- findById ---

    @Nested
    @DisplayName("findById")
    class FindById {

        /**
         * Существующий UUID — возвращает {@code Optional.of(contact)}.
         *
         * <p>Сохраняет контакт и проверяет что {@code findById} находит его по UUID.
         */
        @Test
        @DisplayName("существующий UUID → Optional.of")
        void existingUuid_returnsOptionalOf() {
            final Contact contact = new Contact("John", "Smith", List.of("+1234567890"));
            repository.save(contact);

            final Optional<Contact> found = repository.findById(contact.getId());

            assertTrue(found.isPresent(), "Контакт должен быть найден");
            assertEquals(contact.getId(), found.get().getId());
        }

        /**
         * Несуществующий UUID — возвращает {@code Optional.empty()}.
         *
         * <p>Ищет по случайному UUID, которого нет в БД.
         */
        @Test
        @DisplayName("несуществующий UUID → Optional.empty")
        void nonExistingUuid_returnsOptionalEmpty() {
            final Optional<Contact> found = repository.findById(UUID.randomUUID());

            assertTrue(found.isEmpty(), "Несуществующий UUID должен возвращать пустой Optional");
        }

        /**
         * Контакт с несколькими телефонами — все телефоны присутствуют в результате.
         *
         * <p>Сохраняет контакт с двумя номерами и проверяет что оба присутствуют.
         */
        @Test
        @DisplayName("контакт с несколькими телефонами — все телефоны присутствуют")
        void multiplePhones_allPresent() {
            final Contact contact =
                    new Contact("John", "Smith", List.of("+1234567890", "+1987654321"));
            repository.save(contact);

            final Optional<Contact> found = repository.findById(contact.getId());

            assertTrue(found.isPresent());
            assertEquals(2, found.get().getPhoneNumbers().size());
            assertTrue(found.get().getPhoneNumbers().contains("+1234567890"));
            assertTrue(found.get().getPhoneNumbers().contains("+1987654321"));
        }
    }

    // --- findByLastName ---

    @Nested
    @DisplayName("findByLastName")
    class FindByLastName {

        /**
         * Подстрока в фамилии — находит контакт.
         *
         * <p>Вставляет Smith и Johnson, ищет по подстроке «mit» — должен найтись Smith.
         */
        @Test
        @DisplayName("подстрока в фамилии → находит")
        void substringInLastName_findsContact() throws Exception {
            insertTwoContacts();

            final List<Contact> found = repository.findByLastName("mit");

            assertEquals(1, found.size());
            assertEquals("Smith", found.get(0).getLastName());
        }

        /**
         * Регистронезависимый поиск по фамилии.
         *
         * <p>Ищет «SMITH» в верхнем регистре — должен найтись контакт с фамилией Smith.
         */
        @Test
        @DisplayName("регистронезависимый поиск")
        void caseInsensitive_findsContact() throws Exception {
            insertTwoContacts();

            final List<Contact> found = repository.findByLastName("SMITH");

            assertEquals(1, found.size());
            assertEquals("Smith", found.get(0).getLastName());
        }

        /**
         * Нет совпадений — возвращает пустой список.
         *
         * <p>Ищет несуществующую фамилию «Zzz».
         */
        @Test
        @DisplayName("нет совпадений → пустой список")
        void noMatch_returnsEmptyList() throws Exception {
            insertTwoContacts();

            final List<Contact> found = repository.findByLastName("Zzz");

            assertTrue(found.isEmpty(), "Не должно быть совпадений");
        }
    }

    // --- findByPhoneNumber ---

    @Nested
    @DisplayName("findByPhoneNumber")
    class FindByPhoneNumber {

        /**
         * Подстрока номера — находит контакт.
         *
         * <p>Вставляет контакты, ищет по подстроке «2345» — должен найтись Smith с номером
         * +1234567890.
         */
        @Test
        @DisplayName("подстрока номера → находит контакт")
        void substringInPhone_findsContact() throws Exception {
            insertTwoContacts();

            final List<Contact> found = repository.findByPhoneNumber("2345");

            assertEquals(1, found.size());
            assertEquals("Smith", found.get(0).getLastName());
        }
    }

    // --- findByFirstNameContainingOrLastNameContaining ---

    @Nested
    @DisplayName("findByFirstNameContainingOrLastNameContaining")
    class FindByFirstNameContainingOrLastNameContaining {

        /**
         * Поиск по имени — находит контакт.
         *
         * <p>Ищет «rob» — должен найтись Johnson по имени Robert.
         */
        @Test
        @DisplayName("поиск по имени → находит")
        void searchByFirstName_findsContact() throws Exception {
            insertTwoContacts();

            final List<Contact> found =
                    repository.findByFirstNameContainingOrLastNameContaining("rob");

            assertEquals(1, found.size());
            assertEquals("Robert", found.get(0).getFirstName());
        }

        /**
         * Поиск по фамилии — находит контакт.
         *
         * <p>Ищет «smit» — должен найтись Smith.
         */
        @Test
        @DisplayName("поиск по фамилии → находит")
        void searchByLastName_findsContact() throws Exception {
            insertTwoContacts();

            final List<Contact> found =
                    repository.findByFirstNameContainingOrLastNameContaining("smit");

            assertEquals(1, found.size());
            assertEquals("Smith", found.get(0).getLastName());
        }
    }
}
