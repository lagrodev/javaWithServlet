package ru.hexaend.ex;


public abstract class ApplicationException extends RuntimeException  {
    private final int status;

    public ApplicationException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }
}
