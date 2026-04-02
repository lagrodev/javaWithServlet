package ru.hexaend.rest;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.impl.JdbcContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.repository.jdbc.SchemaInitializer;
import ru.hexaend.repository.jdbc.impl.SingleConnectionDataSourceProvider;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.service.impl.PhoneBookServiceImpl;
import ru.hexaend.util.PhoneValidator;
import ru.hexaend.util.impl.PhoneValidatorImpl;


public class AppContextListener implements ServletContextListener {

    public AppContextListener() {
    }



    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL драйвер не найден", e);
        }
        String host = env("DB_HOST", "localhost");
        String port = env("DB_PORT", "5432");
        String dbName = env("DB_NAME", "phonebook");
        String user = env("DB_USER", "phonebook");
        String password = env("DB_PASSWORD", "phonebook");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        DataSourceProvider ds = new SingleConnectionDataSourceProvider(url, user, password);
        new SchemaInitializer(ds).initialize();

        PhoneValidator validator = new PhoneValidatorImpl();
        ContactRepository repo = new JdbcContactRepository(ds);
        PhoneBookService service = new PhoneBookServiceImpl(
                repo, validator
        );
        sce.getServletContext().setAttribute("service", service);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("service");
    }


    private static String env(String name, String defaultValue)
    {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}
