package com.university.registry.exception;

/**
 * Base unchecked exception for every domain-specific error in the registry
 * application (invalid data, duplicate entities, missing entities, etc.).
 * <p>
 * This class is {@code abstract} on purpose: code should never throw or
 * catch a bare {@code RegistryException} directly. Instead, throw one of
 * the specific subclasses ({@link DuplicateEntityException},
 * {@link EntityNotFoundException}, {@link EntityInUseException},
 * {@link InvalidGradeException}, {@link NoGradesRecordedException}), and
 * handle them centrally in the application's {@code @RestControllerAdvice}
 * global exception handler.
 * <p>
 * This plays the same role as the original {@code MyException} class,
 * but instead of a single class carrying an integer error code that callers had
 * to inspect with {@code if} statements, each error case now has its own dedicated
 * type for clean polymorphic exception handling.
 * <p>
 * This class and its subclasses represent domain rule violations (cases rejected
 * by custom business validation logic rather than low-level infrastructure failures).
 */
public abstract class RegistryException extends RuntimeException
{
    /**
     * Constructs a new {@code RegistryException} with the specified detail message.
     * Protected so that only concrete domain exception subclasses can instantiate it.
     *
     * @param message the human-readable error description
     */
    protected RegistryException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new {@code RegistryException} with the specified detail message and cause.
     * Protected so that only concrete domain exception subclasses can instantiate it.
     *
     * @param message the human-readable error description
     * @param cause   the underlying exception that triggered this domain error
     */
    protected RegistryException(String message, Throwable cause)
    {
        super(message, cause);
    }
}