package ru.hexaend.domain.dto;

import ru.hexaend.domain.mapper.RowMapper;
import ru.hexaend.repository.jdbc.query.QueryExecutor;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Перечисление стратегий выполнения запросов по типу возвращаемого значения метода.
 *
 * <p>Каждая константа определяет, какой метод {@link QueryExecutor} вызвать для получения
 * результата: список, {@link Optional}, количество обновлённых строк или {@code void}.
 * Диспетчеризация выполняется через {@link #of(Method)} на основе возвращаемого типа
 * метода репозитория.
 *
 * @author Vasily Melnik
 */
public enum ReturnedType {
  /** Стратегия для методов, возвращающих {@link List} — вызывает {@code queryList}. */
  LIST {
    @Override
    public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
      return e.queryList(s, m, a);
    }
  },
  /** Стратегия для методов, возвращающих {@link Optional} — вызывает {@code queryOne}. */
  OPTIONAL {
    @Override
    public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
      return e.queryOne(s, m, a);
    }
  },
  /** Стратегия для методов, возвращающих {@code int}/{@code Integer} — вызывает {@code update}. */
  UPDATE {
    @Override
    public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
      return e.update(s, a);
    }
  },
  /** Стратегия для методов с типом {@code void}/{@link Void} — вызывает {@code update}, возвращает {@code null}. */
  VOID {
    @Override
    public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
      e.update(s, a);
      return null;
    }
  };

  /**
   * Карта соответствия Java-типа возвращаемого значения -> стратегия выполнения.
   */
  private static final Map<Class<?>, ReturnedType> DISPATCH = new HashMap<>();

  static {
    DISPATCH.put(void.class, VOID);
    DISPATCH.put(Void.class, VOID);
    DISPATCH.put(List.class, LIST);
    DISPATCH.put(Optional.class, OPTIONAL);
    DISPATCH.put(int.class, UPDATE);
    DISPATCH.put(Integer.class, UPDATE);
  }

  /**
   * Определяет стратегию выполнения по возвращаемому типу метода.
   *
   * <p>Использует карту {@link #DISPATCH} для сопоставления {@code void}/{@link Void} →
   * {@link #VOID}, {@link List} → {@link #LIST}, {@link Optional} → {@link #OPTIONAL},
   * {@code int}/{@code Integer} → {@link #UPDATE}.
   *
   * @param method метод репозитория, тип возвращаемого значения которого определяет стратегию
   * @return константа {@link ReturnedType}, соответствующая типу возвращаемого значения
   * @throws UnsupportedOperationException если тип возвращаемого значения не поддерживается
   */
  public static ReturnedType of(final Method method) {
    final Class<?> returnType = method.getReturnType();
    if (DISPATCH.containsKey(returnType)) {
      return DISPATCH.get(returnType);
    }
    throw new UnsupportedOperationException(
            "Неподдерживаемый тип возвращаемого значения: " + returnType.getName());
  }

  /**
   * Выполняет запрос через {@link QueryExecutor} по стратегии, определённой константой.
   *
   * @param executor исполнитель запросов
   * @param sql       SQL-запрос
   * @param mapper    маппер строк ResultSet в доменные объекты
   * @param args      параметры запроса
   * @return результат выполнения (список, Optional, Integer или {@code null})
   * @throws SQLException если не удалось выполнить запрос
   */
  public abstract Object run(QueryExecutor executor, String sql, RowMapper<?> mapper, Object[] args)
          throws SQLException;
}
