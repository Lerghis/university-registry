package com.university.registry.exception;

/**
 * Thrown when a semester value is not a positive integer (less than {@code 1}).
 * <p>
 * Direct replacement for the {@code MyException.InvalidSemester} error
 * code from the original CSV-based version—used by {@code Student} and
 * {@code Course} whenever their semester is set to zero or a negative value,
 * and typically maps to an HTTP {@code 400 Bad Request} response.
 */
public class InvalidSemesterException extends RegistryException
{
    /**
     * Constructs an {@code InvalidSemesterException} reporting the invalid semester number.
     *
     * @param invalidValue the non-positive semester value supplied by the caller
     */
    public InvalidSemesterException(int invalidValue)
    {
        super("Μη έγκυρη τιμή εξαμήνου: " + invalidValue
                + ". Το εξάμηνο δεν μπορεί να έχει αρνητική τιμή ή τιμή 0.\n");
    }
}