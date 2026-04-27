package ru.hexaend.repository.jdbc.query;

import ru.hexaend.domain.dto.ReturnedType;
import ru.hexaend.domain.mapper.RowMapper;
import ru.hexaend.util.SqlLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
 * @author Vasily Melnik
 */
public class RepositoryInvocationHandler implements InvocationHandler {

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

        final String sql;
        if (!queryAnnotation.fromResource().isEmpty()) {
            sql = SqlLoader.getAsString(queryAnnotation.fromResource());
        } else {
            sql = queryAnnotation.value();
        }
        final Class<? extends RowMapper<?>> mapperClass = mapperAnnotation.value();
        final RowMapper<?> rowMapper = mapperClass.getDeclaredConstructor().newInstance();

        return ReturnedType.of(method).run(executor, sql, rowMapper, args);

    }

}
