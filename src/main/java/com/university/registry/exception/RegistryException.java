package com.university.registry.exception;

/**
 * Base checked exception for every domain-specific error in the registry
 * application (invalid data, duplicate entities, missing entities, etc.).
 * <p>
 * This class is {@code abstract} on purpose: code should never throw or
 * catch a bare {@code RegistryException} directly. Instead, throw one of
 * the specific subclasses ({@link InvalidSemesterException},
 * {@link DuplicateEntityException}, {@link EntityNotFoundException}, ...),
 * and callers can either catch narrowly for a specific case, or broadly
 * with {@code catch (RegistryException e)} when any domain error should be
 * handled the same way (e.g. showing an error dialog in the UI layer).
 * <p>
 * This plays the same role your original {@code MyException} class did,
 * but instead of one class carrying an integer error code that callers had
 * to check with an {@code if}, each error case now gets its own type -
 * which the compiler and IDE can help you catch correctly.
 *
 * This class and it's subclasses represent domain rule violations (things my own validation logic decides to reject).
 */
public abstract class RegistryException extends Exception
{
    // Χρησιμοποιούμε το protected access modifier ώστε μόνο οι subclasses της RegistryException να μπορούν να καλέσουν τον constructor της μέσω του super
    protected RegistryException(String message)
    {
        super(message);
    }

    protected RegistryException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
