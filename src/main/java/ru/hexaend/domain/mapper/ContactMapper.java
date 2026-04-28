package ru.hexaend.domain.mapper;

import ru.hexaend.domain.entity.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Маппер строки {@link ResultSet} в объект {@link Contact}.
 *
 * <p>Маппит только скалярные поля контакта (id, имя, фамилия, метки времени). Список телефонных
 * номеров не заполняется — он загружается отдельным запросом или через JOIN в вызывающем коде.
 *
 * @author Vasily Melnik
 */
public class ContactMapper implements RowMapper<Contact> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Contact mapRow(ResultSet rs) throws SQLException {
    return new Contact(
            UUID.fromString(rs.getString("id")),
            rs.getString("first_name"),
            rs.getString("last_name"),
            List.of(),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime());
  }
}
