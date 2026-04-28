package ru.hexaend.domain.exeptions;

import ru.hexaend.domain.dto.HttpError;

/**
 * Базовое исключение приложения, несущее {@link HttpError}.
 *
 * <p>Все бизнес-исключения наследуются от этого класса. {@link ErrorFilter} перехватывает их и
 * формирует ответ с соответствующим статусом и сообщением.
 *
 * @author Vasily Melnik
 */
public abstract class ApplicationException extends RuntimeException {

  private final HttpError httpError;

  /**
   * @param message   описание ошибки для пользователя
   * @param httpError тип HTTP-ошибки
   */
  protected ApplicationException(String message, HttpError httpError) {
    super(message);
    this.httpError = httpError;
  }

  /**
   * @param message   описание ошибки для пользователя
   * @param cause     исходное исключение
   * @param httpError тип HTTP-ошибки
   */
  protected ApplicationException(String message, Throwable cause, HttpError httpError) {
    super(message, cause);
    this.httpError = httpError;
  }

  /**
   * @return тип HTTP-ошибки, связанный с данным исключением
   */
  public HttpError getHttpError() {
    return httpError;
  }

  /**
   * @return числовой HTTP-статус код
   */
  public int getStatus() {
    return httpError.statusCode();
  }
}
