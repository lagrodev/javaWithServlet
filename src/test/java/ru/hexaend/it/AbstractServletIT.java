package ru.hexaend.it;

import jakarta.servlet.ServletContext;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Базовый класс для HTTP-интеграционных тестов через embedded Tomcat.
 *
 * <p>Расширяет {@link AbstractDatabaseIT}, добавляя встроенный Tomcat с полным webapp (JSP + JSTL)
 * и {@link HttpClient} для отправки запросов. Перед стартом Tomcat устанавливает {@code service} в
 * {@link ServletContext}, чтобы {@link ru.hexaend.rest.AppContextListener} пропустил инициализацию
 * (guard clause).
 *
 * @author Vasily Melnik
 */
public abstract class AbstractServletIT extends AbstractDatabaseIT {

    /**
     * Embedded Tomcat, поднятый на случайном свободном порту.
     */
    private static Tomcat tomcat;

    /**
     * HTTP-клиент без автоматического редиректа (чтобы проверять 302).
     */
    protected static HttpClient httpClient;

    /**
     * Базовый URL Tomcat (например, {@code http://localhost:8080}).
     */
    private static String baseUrl;

    /**
     * Поднимает embedded Tomcat и создаёт {@link HttpClient}.
     *
     * <p>Алгоритм:
     *
     * <ol>
     *   <li>Создаёт Tomcat на порту 0 (случайный свободный порт).
     *   <li>Добавляет webapp из {@code src/main/webapp} — полная JSP + JSTL поддержка.
     *   <li>Регистрирует lifecycle listener на {@code CONFIGURE_START_EVENT}, который устанавливает
     *       {@code service} в {@link ServletContext} до вызова {@code AppContextListener}.
     *   <li>Guard clause в {@code AppContextListener} пропускает инициализацию.
     *   <li>Создаёт {@link HttpClient} с {@link HttpClient.Redirect#NEVER}.
     * </ol>
     */
    @BeforeAll
    static void startTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(0);
        tomcat.setBaseDir(Paths.get("build", "tomcat-it").toAbsolutePath().toString());

        final String webappDir =
                Paths.get("src", "main", "webapp").toAbsolutePath().toString();
        final var context = tomcat.addWebapp("", webappDir);

        // Устанавливаем service в ServletContext ДО AppContextListener
        context.addServletContainerInitializer(
                (c, ctx) -> ctx.setAttribute("service", phoneBookService),
                java.util.Collections.emptySet());

        tomcat.start();

        final int port = tomcat.getConnector().getLocalPort();
        baseUrl = "http://localhost:" + port;

        httpClient =
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();
    }

    /**
     * Останавливает embedded Tomcat после всех тестов.
     */
    @AfterAll
    static void stopTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    /**
     * Возвращает базовый URL Tomcat для формирования запросов.
     *
     * @return базовый URL, например {@code http://localhost:52341}
     */
    protected static String baseUrl() {
        return baseUrl;
    }
}
