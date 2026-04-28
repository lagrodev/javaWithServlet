package ru.hexaend.rest;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ru.hexaend.domain.exeptions.DatabaseException;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.impl.JdbcContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.repository.jdbc.SchemaInitializer;
import ru.hexaend.repository.jdbc.impl.DriverManagerDataSourceProvider;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.service.impl.PhoneBookServiceImpl;
import ru.hexaend.util.PhoneValidator;
import ru.hexaend.util.impl.PhoneValidatorImpl;

/**
 * Слушатель жизненного цикла Servlet-контекста.
 *
 * <p>При старте приложения:
 *
 * <ol>
 *   <li>Загружает JDBC-драйвер PostgreSQL.
 *   <li>Читает параметры подключения к БД из переменных окружения.
 *   <li>Инициализирует схему БД через {@link SchemaInitializer}.
 *   <li>Создаёт и публикует {@link PhoneBookService} в {@code ServletContext}.
 * </ol>
 *
 * При остановке — удаляет сервис из контекста.
 *
 * @author Vasily Melnik
 */
public class AppContextListener implements ServletContextListener {

  /**
   * Возвращает значение переменной окружения или значение по умолчанию.
   *
   * @param name         имя переменной окружения
   * @param defaultValue значение по умолчанию
   * @return значение переменной или {@code defaultValue}
   */
  private static String env(String name, String defaultValue) {
    final String val = System.getenv(name);
    return (val != null && !val.isBlank()) ? val : defaultValue;
  }

  /**
   * Инициализирует приложение: подключение к БД, миграция схемы, создание сервисного слоя и
   * публикация в {@code ServletContext}.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    if (sce.getServletContext().getAttribute("service") != null) {
      return;
    }
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new DatabaseException("PostgreSQL драйвер не найден", e);
    }
    final String host = env("DB_HOST", "localhost");
    final String port = env("DB_PORT", "5432");
    final String dbName = env("DB_NAME", "phonebook");
    final String user = env("DB_USER", "phonebook");
    final String password = env("DB_PASSWORD", "phonebook");

    final String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

    final DataSourceProvider ds = new DriverManagerDataSourceProvider(url, user, password);
    new SchemaInitializer(ds).initialize();

    final PhoneValidator validator = new PhoneValidatorImpl();
    final ContactRepository repo = new JdbcContactRepository(ds);
    final PhoneBookService service = new PhoneBookServiceImpl(repo, validator);
    sce.getServletContext().setAttribute("service", service);
  }

  /**
   * Освобождает ресурсы при остановке приложения.
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    sce.getServletContext().removeAttribute("service");
  }
}
