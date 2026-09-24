package com.university.registry.exception;

/**
 * Thrown when a semester value is not a positive integer.
 * <p>
 * Direct replacement for the {@code MyException.InvalidSemester} error
 * code from the original CSV-based version - used by {@code Student} and
 * {@code Course} whenever their semester is set to a value less than 1.
 */
public class InvalidSemesterException extends RegistryException
{

    public InvalidSemesterException(int invalidValue)
    {
        super("Μη έγκυρη τιμή εξαμήνου: " + invalidValue
                + ". Το εξάμηνο δεν μπορεί να έχει αρνητική τιμή ή τιμή 0.\n");
    }
}
