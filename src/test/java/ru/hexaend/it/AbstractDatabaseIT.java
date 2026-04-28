package ru.hexaend.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.hexaend.repository.impl.JdbcContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.repository.jdbc.SchemaInitializer;
import ru.hexaend.repository.jdbc.impl.DriverManagerDataSourceProvider;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.service.impl.PhoneBookServiceImpl;
import ru.hexaend.util.PhoneValidator;
import ru.hexaend.util.impl.PhoneValidatorImpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Базовый класс для интеграционных тестов с PostgreSQL через Testcontainers.
 *
 * <p>Поднимает единственный экземпляр {@link PostgreSQLContainer} на весь тестовый прогон,
 * инициализирует схему БД через {@link SchemaInitializer} и очищает таблицы после каждого теста.
 * Предоставляет наследникам {@link DataSourceProvider}, {@link JdbcContactRepository} и {@link
 * PhoneBookService}. Контейнер поднимается один раз — не пересоздаётся между тестовыми классами.
 *
 * @author Vasily Melnik
 */
@Testcontainers
public abstract class AbstractDatabaseIT {

    /**
     * PostgreSQL-контейнер, общий для всех интеграционных тестов.
     */
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("phonebook_test")
                    .withUsername("test")
                    .withPassword("test");

    /**
     * SQL-скрипт очистки всех таблиц.
     */
    private static final String CLEAR_SQL = loadResource("db/clear-all.sql");

    /**
     * Провайдер соединений, доступный наследникам.
     */
    protected static DataSourceProvider dataSourceProvider;

    /**
     * Репозиторий контактов, доступный наследникам.
     */
    protected static JdbcContactRepository contactRepository;

    /**
     * Сервис бизнес-логики, доступный наследникам.
     */
    protected static PhoneBookService phoneBookService;

    /**
     * Инициализирует {@link DataSourceProvider}, создаёт схему БД и сервисный слой перед первым
     * тестом.
     *
     * <p>Вызывается один раз за прогон — контейнер уже запущен аннотацией {@code @Container}.
     */
    @BeforeAll
    static void initSchema() {
        dataSourceProvider =
                new DriverManagerDataSourceProvider(
                        POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        new SchemaInitializer(dataSourceProvider).initialize();

        final PhoneValidator validator = new PhoneValidatorImpl();
        contactRepository = new JdbcContactRepository(dataSourceProvider);
        phoneBookService = new PhoneBookServiceImpl(contactRepository, validator);
    }

    /**
     * Читает содержимое ресурса из classpath и возвращает как строку.
     *
     * @param resourcePath путь к ресурсу (например, {@code "db/clear-all.sql"})
     * @return содержимое файла как строка
     */
    private static String loadResource(String resourcePath) {
        try (final InputStream is =
                     AbstractDatabaseIT.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Ресурс не найден: " + resourcePath);
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
            throw new RuntimeException("Ошибка чтения ресурса: " + resourcePath, e);
        }
    }

    /**
     * Очищает все таблицы после каждого теста.
     *
     * <p>Выполняет {@code TRUNCATE contacts CASCADE}, чтобы гарантировать изолированность тестов.
     */
    @AfterEach
    void clearDatabase() throws Exception {
        try (final Connection conn = dataSourceProvider.getConnection();
             final Statement stmt = conn.createStatement()) {
            stmt.execute(CLEAR_SQL);
        }
    }
}
