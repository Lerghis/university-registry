package com.university.registry.exception;

/**
 * This exception class represents an infrastructure problem (the database itself failed), not a business logic one
 */
public class DataAccessException extends RuntimeException
{
    public DataAccessException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
