package ru.hexaend.ex;

/**
 * Перечисление стандартных HTTP-ошибок приложения.
 *
 * <p>Каждая константа несёт числовой статус-код, краткий код (reason phrase)
 * и человекочитаемое описание. Используется в {@link ApplicationException}
 * и {@link ErrorFilter} вместо «магических» чисел.</p>
 *
 * @author Vasily Melnik
 */
public enum HttpError {

    BAD_REQUEST(400, "Bad Request", "Запрос содержит некорректные данные"),
    NOT_FOUND(404, "Not Found", "Запрашиваемый ресурс не найден"),
    CONFLICT(409, "Conflict", "Конфликт при обработке запроса"),
    INTERNAL_ERROR(500, "Internal Server Error", "Внутренняя ошибка сервера");

    private final int statusCode;
    private final String code;
    private final String description;

    HttpError(int statusCode, String code, String description) {
        this.statusCode = statusCode;
        this.code = code;
        this.description = description;
    }

    /**
     * @return числовой HTTP-статус код (например, 404)
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * @return краткий код ошибки (reason phrase, например, {@code "Not Found"})
     */
    public String code() {
        return code;
    }

    /**
     * @return человекочитаемое описание ошибки
     */
    public String description() {
        return description;
    }
}
