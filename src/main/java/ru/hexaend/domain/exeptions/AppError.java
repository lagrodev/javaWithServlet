package ru.hexaend.domain.exeptions;

import ru.hexaend.domain.dto.HttpError;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Неизменяемое представление ошибки, возвращаемое клиенту.
 *
 * <p>Содержит HTTP-статус, краткое наименование ошибки, человекочитаемое сообщение, метку времени и
 * (опционально) карту ошибок валидации полей.
 *
 * @author Vasily Melnik
 */
public class AppError {

  private final int status;
  private final String error;
  private final String message;
  private final Instant timestamp;
  private final Map<String, String> validationErrors;

  /**
   * Создаёт объект ошибки на основе {@link HttpError}.
   *
   * @param httpError тип HTTP-ошибки
   * @param message   описание ошибки для пользователя
   */
  public AppError(HttpError httpError, String message) {
    this(httpError.code(), message, httpError.statusCode(), Collections.emptyMap());
  }

  /**
   * Создаёт объект ошибки с деталями валидации полей.
   *
   * @param error            краткое наименование ошибки
   * @param message          описание ошибки для пользователя
   * @param status           HTTP-статус код
   * @param validationErrors карта «имя поля → описание ошибки»
   */
  public AppError(String error, String message, int status, Map<String, String> validationErrors) {
    this.timestamp = Instant.now();
    this.status = status;
    this.message = message;
    this.error = error;
    this.validationErrors =
            validationErrors != null
                    ? Collections.unmodifiableMap(validationErrors)
                    : Collections.emptyMap();
  }

  /**
   * @return HTTP-статус код ошибки
   */
  public int getStatus() {
    return status;
  }

  /**
   * @return краткое наименование ошибки
   */
  public String getError() {
    return error;
  }

  /**
   * @return человекочитаемое описание ошибки
   */
  public String getMessage() {
    return message;
  }

  /**
   * @return момент возникновения ошибки
   */
  public Instant getTimestamp() {
    return timestamp;
  }

  /**
   * @return неизменяемая карта ошибок валидации (пустая, если валидация не применялась)
   */
  public Map<String, String> getValidationErrors() {
    return validationErrors;
  }
}
