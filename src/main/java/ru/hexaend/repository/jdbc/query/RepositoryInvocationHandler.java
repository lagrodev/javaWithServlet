package ru.hexaend.repository.jdbc.query;

import ru.hexaend.repository.jdbc.mapper.RowMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Обработчик вызовов для динамического прокси репозитория.
 *
 * <p>Реализует паттерн Dynamic Proxy: перехватывает вызовы методов
 * интерфейса репозитория, извлекает SQL из аннотации {@link Query},
 * маппер из {@link Mapper} и делегирует выполнение в {@link QueryExecutor}.</p>
 *
 * <p>Тип возвращаемого значения метода определяет стратегию выполнения:
 * {@code List} — queryList, {@code Optional} — queryOne,
 * {@code int/Integer} — update, {@code void} — update без возврата.</p>
 *
 * @param <T> тип сущности репозитория
 * @author Vasily Melnik
 */
public class RepositoryInvocationHandler<T> implements InvocationHandler {

    private final QueryExecutor executor;

    /**
     * @param executor исполнитель SQL-запросов
     */
    public RepositoryInvocationHandler(QueryExecutor executor) {
        this.executor = executor;
    }

    /**
     * Обрабатывает вызов метода прокси-репозитория.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Методы {@link Object} (toString, equals, hashCode) делегируются напрямую.</li>
     *   <li>Извлекаются аннотации {@link Query} и {@link Mapper} с вызванного метода.</li>
     *   <li>По типу возвращаемого значения определяется стратегия выполнения
     *       ({@code List}, {@code Optional}, {@code int/void}) через enum {@link ReturnedType}.</li>
     *   <li>Запрос выполняется через {@link QueryExecutor} с соответствующим маппером.</li>
     * </ol></p>
     *
     * @throws UnsupportedOperationException если метод не аннотирован {@link Query}/{@link Mapper}
     *                                       или тип возвращаемого значения не поддерживается
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Методы Object (toString, equals, hashCode) вызываем напрямую
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        final Query queryAnnotation = method.getAnnotation(Query.class);
        final Mapper mapperAnnotation = method.getAnnotation(Mapper.class);

        if (queryAnnotation == null) {
            throw new UnsupportedOperationException(
                    "Метод " + method.getName() + " не аннотирован @Query");
        }
        if (mapperAnnotation == null) {
            throw new UnsupportedOperationException(
                    "Метод " + method.getName() + " не аннотирован @Mapper, нужно указать класс Маппера для Маппинга" +
                            " результата запроса"
            );
        }

        final String sql = queryAnnotation.value();
        final Class<? extends RowMapper<?>> mapperClass = mapperAnnotation.value();
        final RowMapper<?> rowMapper = mapperClass.getDeclaredConstructor().newInstance();

        return ReturnedType.of(method).run(executor, sql, rowMapper, args);

    }

    private enum ReturnedType {
        LIST {
            @Override
            public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
                return e.queryList(s, m, a);
            }
        },
        OPTIONAL {
            @Override
            public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
                return e.queryOne(s, m, a);
            }
        },
        UPDATE {
            @Override
            public Object run(QueryExecutor e, String s, RowMapper<?> m, Object[] a) {
                return e.update(s, a);
            }
        },
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


        static ReturnedType of(final Method method) {
            final Class<?> returnType = method.getReturnType();
            if (DISPATCH.containsKey(returnType)) {
                return DISPATCH.get(returnType);
            }
            throw new UnsupportedOperationException(
                    "Неподдерживаемый тип возвращаемого значения: " + returnType.getName());
        }


        public abstract Object run(
                QueryExecutor executor, String sql, RowMapper<?> mapper, Object[] args) throws SQLException;

    }
}
