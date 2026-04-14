package ru.hexaend.repository.jdbc.fabric;

import ru.hexaend.repository.jdbc.query.QueryExecutor;
import ru.hexaend.repository.jdbc.query.RepositoryInvocationHandler;

import java.lang.reflect.Proxy;

public class RepositoryFactory {
    private final QueryExecutor executor;

    public RepositoryFactory(QueryExecutor executor) {
        this.executor = executor;
    }

    @SuppressWarnings("unchecked")
    public <R> R createRepository(Class<R> repositoryInterface) {
        return (R) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class[]{repositoryInterface},
                new RepositoryInvocationHandler<>(executor)
        );
    }
}
