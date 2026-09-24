package com.university.registry.exception;

public class InvalidGradeException extends RegistryException
{
    public InvalidGradeException(float invalidValue)
    {
        super ("Μη έγκυρη τιμή βαθμού: " + invalidValue
                + ". Το εύρος του βαθμού είναι 0 - 10.\n");
    }
}
