package ru.hexaend.domain.dto;

import ru.hexaend.domain.mapper.RowMapper;
import ru.hexaend.repository.jdbc.query.QueryExecutor;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum ReturnedType {
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


    public static ReturnedType of(final Method method) {
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
