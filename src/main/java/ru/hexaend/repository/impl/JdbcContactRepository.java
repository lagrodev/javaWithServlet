package ru.hexaend.repository.impl;

import ru.hexaend.entity.Contact;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;

import java.sql.*;
import java.util.*;

/**
 * Реализация ContactRepository через JDBC.
 * UUID хранится в БД как VARCHAR(36) — портируемо между SQLite / PostgreSQL / MySQL.
 * <p>
 * Схема:
 * contacts      (id PK VARCHAR(36), first_name, last_name)
 * phone_numbers (id PK AI, contact_id FK VARCHAR(36), phone)
 */
public class JdbcContactRepository implements ContactRepository
{

    private final DataSourceProvider dataSourceProvider;

    public JdbcContactRepository(DataSourceProvider dataSourceProvider)
    {
        this.dataSourceProvider = dataSourceProvider;
    }

    // ─── Save (upsert: delete phones → re-insert) ────────────────────────────

    @Override
    public void save(Contact contact)
    {
        String upsertContact = """
                INSERT INTO contacts (id, first_name, last_name)
                VALUES (?, ?, ?)
                ON CONFLICT(id) DO UPDATE
                    SET first_name = excluded.first_name,
                        last_name  = excluded.last_name
                """;
        String deletePhones = "DELETE FROM phone_numbers WHERE contact_id = ?";
        String insertPhone = "INSERT INTO phone_numbers (contact_id, phone) VALUES (?, ?)";
        String idStr = contact.getId().toString();

        try (Connection conn = dataSourceProvider.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                try (PreparedStatement ps = conn.prepareStatement(upsertContact))
                {
                    ps.setString(1, idStr);
                    ps.setString(2, contact.getFirstName());
                    ps.setString(3, contact.getLastName());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(deletePhones))
                {
                    ps.setString(1, idStr);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(insertPhone))
                {
                    for (String phone : contact.getPhoneNumbers())
                    {
                        ps.setString(1, idStr);
                        ps.setString(2, phone);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
            } catch (SQLException e)
            {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка сохранения контакта", e);
        }
    }

    @Override
    public void delete(Contact contact)
    {
        deleteById(contact.getId());
    }


    @Override
    public void deleteById(UUID id)
    {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка удаления контакта", e);
        }
    }

    // ─── Find by ID ──────────────────────────────────────────────────────────

    @Override
    public Optional<Contact> findById(UUID id)
    {
        String sql = """
                SELECT c.id, c.first_name, c.last_name, p.phone
                FROM contacts c
                LEFT JOIN phone_numbers p ON p.contact_id = c.id
                WHERE c.id = ?
                ORDER BY p.id
                """;
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery())
            {
                return mapFirstContact(rs);
            }
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка поиска контакта по ID", e);
        }
    }

    // ─── Find all ────────────────────────────────────────────────────────────

    @Override
    public List<Contact> findAll()
    {
        String sql = """
                SELECT c.id, c.first_name, c.last_name, p.phone
                FROM contacts c
                LEFT JOIN phone_numbers p ON p.contact_id = c.id
                ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
                """;
        return queryContacts(sql);
    }

    // ─── Find by last name ───────────────────────────────────────────────────

    @Override
    public List<Contact> findByLastName(String lastName)
    {
        String sql = """
                SELECT c.id, c.first_name, c.last_name, p.phone
                FROM contacts c
                LEFT JOIN phone_numbers p ON p.contact_id = c.id
                WHERE LOWER(c.last_name) LIKE ?
                ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
                """;
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, "%" + lastName.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery())
            {
                return mapContacts(rs);
            }
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка поиска по фамилии", e);
        }
    }

    // ─── Find by phone ───────────────────────────────────────────────────────

    @Override
    public List<Contact> findByPhoneNumber(String phoneNumber)
    {
        String sql = """
                SELECT c.id, c.first_name, c.last_name, p.phone
                FROM contacts c
                JOIN phone_numbers p ON p.contact_id = c.id
                WHERE p.phone LIKE ?
                ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
                """;
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, "%" + phoneNumber + "%");
            try (ResultSet rs = ps.executeQuery())
            {
                return mapContacts(rs);
            }
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка поиска по телефону", e);
        }
    }

    @Override
    public List<Contact> findByFirstNameContainingOrLastNameContaining(String query)
    {
        String sql = """
                SELECT c.id, c.first_name, c.last_name, p.phone
                FROM contacts c
                LEFT JOIN phone_numbers p ON p.contact_id = c.id
                WHERE LOWER(c.first_name) LIKE ? OR LOWER(c.last_name) LIKE ?
                ORDER BY LOWER(c.last_name), LOWER(c.first_name), p.phone
                """;
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            String param = "%" + query.toLowerCase() + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            try (ResultSet rs = ps.executeQuery())
            {
                return mapContacts(rs);
            }
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка поиска по имени или фамилии", e);
        }
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    private List<Contact> queryContacts(String sql)
    {
        try (Connection conn = dataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            return mapContacts(rs);
        } catch (SQLException e)
        {
            throw new RuntimeException("Ошибка запроса контактов", e);
        }
    }

    private List<Contact> mapContacts(ResultSet rs) throws SQLException
    {
        Map<UUID, ContactBuilder> builders = new LinkedHashMap<>();
        while (rs.next())
        {
            UUID id = UUID.fromString(rs.getString("id"));
            String phone = rs.getString("phone");
            ContactBuilder b = builders.computeIfAbsent(id, key -> {
                try
                {
                    return new ContactBuilder(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("first_name"),
                            rs.getString("last_name"));
                } catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }
            });
            if (phone != null)
            {
                b.addPhone(phone);
            }
        }
        List<Contact> result = new ArrayList<>();
        for (ContactBuilder b : builders.values())
        {
            if (!b.phones.isEmpty())
            {
                result.add(b.build());
            }
        }
        return result;
    }

    private Optional<Contact> mapFirstContact(ResultSet rs) throws SQLException
    {
        List<Contact> list = mapContacts(rs);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }


    private static class ContactBuilder
    {
        final UUID id;
        final String firstName;
        final String lastName;
        final List<String> phones = new ArrayList<>();

        ContactBuilder(UUID id, String firstName, String lastName)
        {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        void addPhone(String phone)
        {
            phones.add(phone);
        }

        Contact build()
        {
            return new Contact(id, firstName, lastName, phones);
        }
    }
}