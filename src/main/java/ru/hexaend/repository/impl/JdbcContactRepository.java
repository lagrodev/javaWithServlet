package ru.hexaend.repository.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC-реализация {@link ContactRepository} для работы с PostgreSQL.
 *
 * <p>Выполняет CRUD-операции над таблицами {@code contacts} и {@code phone_numbers}.
 * Все запросы выполняются через {@link DataSourceProvider}, соединения закрываются
 * через try-with-resources. Операция {@link #save} выполняется в транзакции.</p>
 *
 * @author Vasily Melnik
 */
public class JdbcContactRepository implements ContactRepository {



    /**
     * Базовые колонки SELECT-запроса контакта с телефонами.
     */
    private static final String SELECT_COLUMNS =
            "c.id, c.first_name, c.last_name, p.phone, c.created_at, c.updated_at";

    /**
     * FROM + LEFT JOIN для получения контактов вместе с телефонами.
     */
    private static final String FROM_WITH_PHONES =
            "FROM contacts c LEFT JOIN phone_numbers p ON p.contact_id = c.id";

    /**
     * Стандартная сортировка: по фамилии, имени, номеру телефона (регистронезависимо).
     */
    private static final String ORDER_DEFAULT =
            "ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone";

    private final DataSourceProvider dataSourceProvider;

    /**
     * @param dataSourceProvider провайдер JDBC-соединений
     */
    public JdbcContactRepository(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    /**
     * Сохраняет контакт (вставка или обновление) в рамках транзакции.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>UPSERT записи в таблицу {@code contacts}.</li>
     *   <li>Удаление всех существующих телефонов контакта.</li>
     *   <li>Пакетная вставка актуальных телефонов.</li>
     * </ol>
     * При ошибке на любом шаге транзакция откатывается.</p>
     *
     * @param contact контакт для сохранения
     */
    @Override
    public void save(Contact contact) {
        final String upsertContact = """
                INSERT INTO contacts (id, first_name, last_name, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                    SET first_name = excluded.first_name,
                        last_name  = excluded.last_name,
                        updated_at = excluded.updated_at
                """;
        final String deletePhones = "DELETE FROM phone_numbers WHERE contact_id = ?";
        final String insertPhone = "INSERT INTO phone_numbers (contact_id, phone) VALUES (?, ?)";
        final String idStr = contact.getId().toString();

        try (final Connection conn = dataSourceProvider.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (final PreparedStatement ps = conn.prepareStatement(upsertContact)) {
                    ps.setString(1, idStr);
                    ps.setString(2, contact.getFirstName());
                    ps.setString(3, contact.getLastName());
                    ps.setTimestamp(4, Timestamp.valueOf(contact.getCreatedAt()));
                    ps.setTimestamp(5, Timestamp.valueOf(contact.getUpdatedAt()));
                    ps.executeUpdate();
                }
                try (final PreparedStatement ps = conn.prepareStatement(deletePhones)) {
                    ps.setString(1, idStr);
                    ps.executeUpdate();
                }
                try (final PreparedStatement ps = conn.prepareStatement(insertPhone)) {
                    for (String phone : contact.getPhoneNumbers()) {
                        ps.setString(1, idStr);
                        ps.setString(2, phone);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения контакта", e);
        }
    }

    /**
     * Удаляет контакт.
     *
     * @param contact контакт для удаления
     */
    @Override
    public void delete(Contact contact) {
        deleteById(contact.getId());
    }

    /**
     * Удаляет контакт по UUID. Телефоны удаляются каскадно (ON DELETE CASCADE).
     *
     * @param id идентификатор контакта
     */
    @Override
    public void deleteById(UUID id) {
        final String sql = "DELETE FROM contacts WHERE id = ?";
        try (final Connection conn = dataSourceProvider.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления контакта", e);
        }
    }

    /**
     * Возвращает список всех контактов.
     *
     * @return список контактов
     */
    @Override
    public List<Contact> findAll() {
        final String sql = "SELECT " + SELECT_COLUMNS + " " + FROM_WITH_PHONES + " " + ORDER_DEFAULT;
        return queryWithPhones(sql);
    }

    /**
     * Возвращает список контактов по фамилии.
     *
     * @param lastName фамилия для поиска
     * @return список контактов
     */
    @Override
    public List<Contact> findByLastName(String lastName) {
        final String sql = "SELECT " + SELECT_COLUMNS + " " + FROM_WITH_PHONES
                + " WHERE LOWER(c.last_name) LIKE ? " + ORDER_DEFAULT;
        return queryWithPhones(sql, "%" + lastName.toLowerCase() + "%");
    }

    /**
     * Возвращает контакт по UUID.
     *
     * @param id идентификатор контакта
     * @return контакт или пустая опция, если не найден
     */
    @Override
    public Optional<Contact> findById(UUID id) {
        final String sql = "SELECT " + SELECT_COLUMNS + " " + FROM_WITH_PHONES
                + " WHERE c.id = ? ORDER BY p.id";
        final List<Contact> list = queryWithPhones(sql, id.toString());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    /**
     * Возвращает список контактов по номеру телефона.
     *
     * @param phoneNumber номер телефона для поиска
     * @return список контактов
     */
    @Override
    public List<Contact> findByPhoneNumber(String phoneNumber) {
        final String sql = "SELECT " + SELECT_COLUMNS
                + " FROM contacts c JOIN phone_numbers p ON p.contact_id = c.id"
                + " WHERE p.phone LIKE ? " + ORDER_DEFAULT;
        return queryWithPhones(sql, "%" + phoneNumber + "%");
    }

    /**
     * Возвращает список контактов по имени или фамилии.
     *
     * @param query строка для поиска
     * @return список контактов
     */
    @Override
    public List<Contact> findByFirstNameContainingOrLastNameContaining(String query) {
        final String sql = "SELECT " + SELECT_COLUMNS + " " + FROM_WITH_PHONES
                + " WHERE LOWER(c.first_name) LIKE ? OR LOWER(c.last_name) LIKE ? " + ORDER_DEFAULT;
        final String param = "%" + query.toLowerCase() + "%";
        return queryWithPhones(sql, param, param);
    }

    /**
     * Выполняет запрос с телефонами.
     *
     * @param sql    запрос
     * @param params параметры запроса
     * @return список контактов
     */
    private List<Contact> queryWithPhones(String sql, String... params) {
        try (final Connection conn = dataSourceProvider.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (final ResultSet rs = ps.executeQuery()) {
                return mapContacts(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка запроса контактов", e);
        }
    }

    /**
     * Маппит {@link ResultSet} с JOIN-результатом в список контактов.
     *
     * <p>Группирует строки по UUID контакта с помощью {@link LinkedHashMap},
     * собирая телефоны в {@link ContactBuilder}. Порядок вставки сохраняется.</p>
     */
    private List<Contact> mapContacts(ResultSet rs) throws SQLException {
        final Map<UUID, ContactBuilder> builders = new LinkedHashMap<>();
        while (rs.next()) {
            final UUID id = UUID.fromString(rs.getString("id"));
            final String phone = rs.getString("phone");
            final ContactBuilder b = builders.computeIfAbsent(id, key -> {
                try {
                    return new ContactBuilder(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            if (phone != null) {
                b.addPhone(phone);
            }
        }
        final List<Contact> result = new ArrayList<>();
        for (ContactBuilder b : builders.values()) {
            if (!b.phones.isEmpty()) {
                result.add(b.build());
            }
        }
        return result;
    }

    private Optional<Contact> mapFirstContact(ResultSet rs) throws SQLException {
        final List<Contact> list = mapContacts(rs);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }


    /**
     * Вспомогательный билдер для сборки {@link Contact} из нескольких строк ResultSet.
     * Аккумулирует телефонные номера, принадлежащие одному контакту.
     */
    private static class ContactBuilder {
        final UUID id;
        final String firstName;
        final String lastName;
        final List<String> phones = new ArrayList<>();
        final LocalDateTime createdAt;
        final LocalDateTime updatedAt;

        ContactBuilder(UUID id, String firstName, String lastName, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        void addPhone(String phone) {
            phones.add(phone);
        }

        Contact build() {
            return new Contact(id, firstName, lastName, phones);
        }
    }
}