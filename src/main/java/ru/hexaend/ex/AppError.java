package ru.hexaend.ex;


import java.time.Instant;
import java.util.Map;


public class AppError {
    private int status;
    private String error;
    private String message;
    private Instant timestamp;
    private Map<String, String> validationErrors;

    public AppError(String error, String message, int status){
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.error = error;
    }


    public AppError(String error, String message, int status, Map<String, String> validationErrors) {
        this(error, message, status); // Вызываем базовый конструктор
        this.validationErrors = validationErrors;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp)
    {
        this.timestamp = timestamp;
    }

    public Map<String, String> getValidationErrors()
    {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors)
    {
        this.validationErrors = validationErrors;
    }
}
