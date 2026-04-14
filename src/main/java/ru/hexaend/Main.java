package ru.hexaend;

import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.impl.InMemoryContactRepository;
import ru.hexaend.repository.impl.JdbcContactRepository;
import ru.hexaend.repository.jdbc.DataSourceProvider;
import ru.hexaend.repository.jdbc.SchemaInitializer;
import ru.hexaend.repository.jdbc.impl.SingleConnectionDataSourceProvider;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.util.PhoneValidator;
import ru.hexaend.service.impl.PhoneBookServiceImpl;
import ru.hexaend.util.impl.PhoneValidatorImpl;
import ru.hexaend.ui.ConsoleUi;

public class Main {
    static void main() {
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
        PhoneBookService service = new PhoneBookServiceImpl(repo, validator);
        ConsoleUi ui = new ConsoleUi(service);

        ui.start();
    }

    private static String env(String name, String defaultValue)
    {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

}

