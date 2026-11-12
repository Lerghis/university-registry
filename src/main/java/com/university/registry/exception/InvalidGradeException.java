package com.university.registry.exception;

/**
 * Thrown when a grade value falls outside the valid academic scale of {@code 0} to {@code 10}.
 * <p>
 * Enforces fail-fast domain validation inside the {@code Grade} model before
 * any persistence operation is attempted, and typically maps to an HTTP
 * {@code 400 Bad Request} response.
 */
public class InvalidGradeException extends RegistryException
{
    /**
     * Constructs an {@code InvalidGradeException} reporting the out-of-range value.
     *
     * @param invalidValue the invalid grade value supplied by the caller
     */
    public InvalidGradeException(float invalidValue)
    {
        super("Μη έγκυρη τιμή βαθμού: " + invalidValue
                + ". Το εύρος του βαθμού είναι 0 - 10.\n");
    }
}