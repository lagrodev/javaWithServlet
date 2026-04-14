package ru.hexaend.repository.jdbc.query;

import ru.hexaend.repository.jdbc.mapper.RowMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositoryInvocationHandler<T> implements InvocationHandler {

    private final QueryExecutor executor;

    public RepositoryInvocationHandler(QueryExecutor executor) {
        this.executor = executor;
    }

    /**
     * короче, тут я запарился, так что, спер идею с использованием enum для "полиморфизма"
     * (тут Борисов должен был биться в экстазе), вместо 5 if
     * и потом спер идею Косенко: определить в Map класс возвращаемого типа (он говорил, что в gameDev такое практикуют)
     * ток он говорил, что в Map прописывают все интерфейсы, которые нужны? юзаются?
     * чет такое, слушать надо было :(
     * в общем, если тип возвращаемого значения не поддерживается, то кидаем UnsupportedOperationException
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) { // короче, метод класса obj просто вызываем, не обрабатывая
            return method.invoke(this, args);
        }
        Query queryAnnotation = method.getAnnotation(Query.class);
        Mapper mapperAnnotation = method.getAnnotation(Mapper.class);
        if (queryAnnotation == null) {
            throw new UnsupportedOperationException(
                    "Метод " + method.getName() + " не аннотирован @Query");
        }
        if (mapperAnnotation == null) {
            throw  new UnsupportedOperationException(
                    "Метод " + method.getName() + " не аннотирован @Mapper, нужно указать класс Маппера для Маппинга" +
                            " результата запроса"
            );
        }

        String sql = queryAnnotation.value();
        Class<? extends RowMapper<?>> mapperClass = mapperAnnotation.value();
        RowMapper<?> rowMapper = mapperClass.getDeclaredConstructor().newInstance();

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

        private static final Map<Class<?>, ReturnedType> DISPATCH = new HashMap<>(); // косенко, привет


        static {
            DISPATCH.put(void.class, VOID);
            DISPATCH.put(Void.class, VOID);
            DISPATCH.put(List.class, LIST);
            DISPATCH.put(Optional.class, OPTIONAL);
            DISPATCH.put(int.class, UPDATE);
            DISPATCH.put(Integer.class, UPDATE);
        }


        static ReturnedType of(final Method method)  {
            Class<?> returnType = method.getReturnType();
            if (DISPATCH.containsKey(returnType))
            {
                return DISPATCH.get(returnType);
            }
            throw new UnsupportedOperationException(
                    "Неподдерживаемый тип возвращаемого значения: " + returnType.getName());
        }


        public abstract Object run(
                QueryExecutor executor, String sql, RowMapper<?> mapper, Object[] args) throws SQLException;

    }
}
