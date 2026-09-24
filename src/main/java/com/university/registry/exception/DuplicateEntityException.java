package com.university.registry.exception;

/**
 * Thrown when an operation would create a duplicate entity - for example,
 * adding a Student whose AM (registration number) already exists, a
 * Professor whose PID already exists, or a Course whose CID already exists.
 * <p>
 * This replaces the {@code boolean} return value pattern used by
 * {@code ControlPersonFlow()} / {@code ControlCourseFlow()} in the original
 * {@code DataBase} class. Returning {@code false} on failure forces every
 * caller to remember to check it; throwing an exception makes the failure
 * impossible to silently ignore.
 */
public class DuplicateEntityException extends RegistryException
{

    public DuplicateEntityException(String entityType, String identifier)
    {
        super(entityType + " με το αναγνωριστικό '" + identifier + "' υπάρχει ήδη.\n");
    }
}
