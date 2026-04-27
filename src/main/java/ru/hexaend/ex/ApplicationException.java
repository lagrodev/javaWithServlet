package ru.hexaend.ex;

/**
 * Базовое исключение приложения, несущее HTTP-статус код.
 *
 * <p>Все бизнес-исключения наследуются от этого класса.
 * {@link ErrorFilter} перехватывает их и формирует ответ
 * с соответствующим статусом и сообщением.</p>
 *
 * @author Vasily Melnik
 */
public abstract class ApplicationException extends RuntimeException {

    private final int status;

    /**
     * @param message описание ошибки для пользователя
     * @param status  HTTP-статус код (например, 400, 404)
     */
    protected ApplicationException(String message, int status) {
        super(message);
        this.status = status;
    }

    /**
     * @return HTTP-статус код, связанный с данным исключением
     */
    public int getStatus() {
        return status;
    }
}
