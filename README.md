# Телефонный справочник (Java Servlet)

> Веб- и консольное приложение для управления контактами и телефонными номерами.
> Построено на чистых **Java Servlets / JSP / JDBC** без ORM и тяжёлых фреймворков.

---

## Технологический стек

| Слой              | Технология                                      |
|-------------------|-------------------------------------------------|
| Язык              | Java 21                                          |
| Сборка            | Gradle (Kotlin DSL)                              |
| Web-сервер        | Apache Tomcat 10.1 (Jakarta Servlet 6.0)         |
| Шаблоны           | JSP + JSTL 3.0                                   |
| База данных       | PostgreSQL 15+                                   |
| Доступ к данным   | JDBC (без ORM)                                   |
| Контейнеризация   | Docker, Docker Compose                           |

## Архитектура

```
ru.hexaend
├── domain           # Бизнес-константы (ContactConstraints)
├── entity           # Сущности (AbstractEntity, Contact)
├── ex               # Обработка ошибок (AppError, ApplicationException, ErrorFilter)
│   └── custom       # Конкретные исключения (ContactNotFoundException, ValidationException)
├── repository       # Интерфейсы репозиториев (MyRepository, ContactRepository)
│   ├── impl         # Реализации (JdbcContactRepository, InMemoryContactRepository)
│   └── jdbc         # JDBC-инфраструктура
│       ├── fabric   # Фабрика прокси-репозиториев (RepositoryFactory)
│       ├── impl     # Провайдер соединений (DriverManagerDataSourceProvider)
│       ├── mapper    # Мапперы ResultSet → объект (ContactMapper, PhoneMapper)
│       └── query     # Аннотации @Query/@Mapper, QueryExecutor, InvocationHandler
├── rest             # Servlet-слой (контроллеры, фильтры, слушатели)
├── service          # Бизнес-логика (PhoneBookService)
│   └── impl         # PhoneBookServiceImpl
├── ui               # Консольный интерфейс (ConsoleUi)
│   └── command      # Команды консоли (Command, ConsoleHelper)
│       └── impl     # Реализации команд (AddContact, EditContact, Delete, Search…)
└── util             # Утилиты (PhoneValidator)
    └── impl         # PhoneValidatorImpl
```

### Ключевые решения

- **Dynamic Proxy для репозиториев** — методы интерфейса аннотируются `@Query` и `@Mapper`;
  прокси через `RepositoryInvocationHandler` автоматически выполняет SQL и маппит результат.
- **Ручной JDBC-репозиторий** (`JdbcContactRepository`) — для сложных операций (транзакционный save с UPSERT + batch insert телефонов).
- **Глобальная обработка ошибок** — `ErrorFilter` перехватывает `ApplicationException` и перенаправляет на `error.jsp`.
- **Иммутабельный `AppError`** — все поля `final`, без сеттеров.
- **Валидация** — телефоны проверяются через `PhoneValidator` (regex + подсчёт цифр).

---

## Быстрый старт

### Предварительные требования

- **Docker** и **Docker Compose** (или локальный PostgreSQL)
- **JDK 21+** (для локальной сборки без Docker)

### Запуск через Docker Compose

```bash
docker compose up --build
```

Приложение будет доступно по адресу: **http://localhost:8080**

### Локальная сборка и запуск

```bash
# 1. Запустить PostgreSQL (например, через Docker)
docker compose up -d db

# 2. Собрать WAR
./gradlew war

# 3. Скопировать WAR в Tomcat
cp build/libs/javaWithServlet.war $CATALINA_HOME/webapps/ROOT.war

# 4. Запустить Tomcat
$CATALINA_HOME/bin/startup.sh
```

### Переменные окружения

| Переменная    | По умолчанию | Описание                 |
|---------------|-------------|--------------------------|
| `DB_HOST`     | `localhost` | Хост PostgreSQL          |
| `DB_PORT`     | `5432`      | Порт PostgreSQL          |
| `DB_NAME`     | `phonebook` | Имя базы данных          |
| `DB_USER`     | `phonebook` | Имя пользователя БД     |
| `DB_PASSWORD` | `phonebook` | Пароль пользователя БД   |

---

## Функциональность

### Web-интерфейс

| Маршрут           | Метод | Описание                         |
|-------------------|-------|----------------------------------|
| `/contacts`       | GET   | Список всех контактов            |
| `/contacts`       | POST  | Удаление контакта (action=delete)|
| `/contacts/form`  | GET   | Форма создания / редактирования  |
| `/contacts/form`  | POST  | Сохранение контакта              |

### Консольный интерфейс

1. Показать все контакты
2. Добавить контакт
3. Редактировать контакт
4. Удалить контакт
5. Поиск по фамилии
6. Поиск по номеру телефона
7. Добавить номер к существующему контакту
0. Выход

---

## Структура БД

```sql
-- Таблица контактов
CREATE TABLE contacts (
    id         VARCHAR(36) PRIMARY KEY,   -- UUID (генерируется приложением)
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Таблица телефонных номеров (FK → contacts, каскадное удаление)
CREATE TABLE phone_numbers (
    id         SERIAL PRIMARY KEY,
    contact_id VARCHAR(36) NOT NULL,
    phone      VARCHAR(50) NOT NULL,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
);
```

---

## Автор

**Vasily Melnik** — ВГУ, 6 семестр    