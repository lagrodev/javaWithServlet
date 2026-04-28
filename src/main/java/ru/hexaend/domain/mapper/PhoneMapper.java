package ru.hexaend.domain.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер строки {@link ResultSet} в строку с номером телефона.
 *
 * <p>Извлекает значение колонки {@code phone} из результата запроса к таблице {@code
 * phone_numbers}.
 *
 * @author Vasily Melnik
 */
public class PhoneMapper implements RowMapper<String> {

  /**
   * {@inheritDoc}
   */
  @Override
  public String mapRow(ResultSet rs) throws SQLException {
    return rs.getString("phone");
  }
}
