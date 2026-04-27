package ru.hexaend.repository.jdbc.fabric;

import ru.hexaend.repository.jdbc.query.QueryExecutor;
import ru.hexaend.repository.jdbc.query.RepositoryInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * Фабрика для создания реализаций репозиториев через Dynamic Proxy.
 *
 * <p>Принимает интерфейс репозитория и создаёт его прокси-реализацию,
 * в которой каждый метод обрабатывается {@link RepositoryInvocationHandler}:
 * SQL берётся из аннотации {@code @Query}, маппер — из {@code @Mapper}.</p>
 *
 * @author Vasily Melnik
 */
public class RepositoryFactory {

    private final QueryExecutor executor;

    /**
     * @param executor исполнитель SQL-запросов, используемый прокси-репозиториями
     */
    public RepositoryFactory(QueryExecutor executor) {
        this.executor = executor;
    }

    /**
     * Создаёт прокси-реализацию указанного интерфейса репозитория.
     *
     * @param repositoryInterface интерфейс репозитория с аннотированными методами
     * @param <R>                 тип интерфейса
     * @return прокси-объект, реализующий указанный интерфейс
     */
    @SuppressWarnings("unchecked")
    public <R> R createRepository(Class<R> repositoryInterface) {
        return (R) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class[]{repositoryInterface},
                new RepositoryInvocationHandler<>(executor)
        );
    }
}
