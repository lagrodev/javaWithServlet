package ru.hexaend.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Утилитный класс для загрузки SQL-запросов из classpath-ресурсов.
 *
 * <p>Читает {@code .sql} файлы из {@code src/main/resources/} и возвращает их содержимое как
 * строку. Результаты кэшируются для избежания повторного чтения с диска.
 *
 * @author Vasily Melnik
 */
public final class SqlLoader {

    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    private SqlLoader() {
    }

    /**
     * Читает содержимое ресурса из classpath и возвращает как строку.
     *
     * <p>При первом вызове ресурс читается с диска и помещается в кэш. Последующие вызовы возвращают
     * закэшированное значение.
     *
     * @param resourcePath путь к ресурсу (например, {@code "sql/contact/find-all.sql"})
     * @return содержимое файла как строка (без ведущих/конечных пробелов)
     * @throws IllegalArgumentException если ресурс не найден в classpath
     */
    public static String getAsString(String resourcePath) {
        return CACHE.computeIfAbsent(
                resourcePath,
                path -> {
                    try (final InputStream is = SqlLoader.class.getClassLoader().getResourceAsStream(path)) {
                        if (is == null) {
                            throw new IllegalArgumentException("SQL-ресурс не найден: " + path);
                        }
                        try (final BufferedReader reader =
                                     new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                            final StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line).append('\n');
                            }
                            return sb.toString().stripTrailing();
                        }
                    } catch (Exception e) {
                        if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
                        throw new IllegalArgumentException("Ошибка чтения SQL-ресурса: " + path, e);
                    }
                });
    }
}
