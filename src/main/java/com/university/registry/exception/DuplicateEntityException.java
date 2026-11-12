package com.university.registry.exception;

/**
 * Thrown when an operation would create a duplicate entity—for example,
 * adding a {@code Student} whose AM already exists, a {@code Professor}
 * whose ID already exists, or a {@code Course} whose ID already exists.
 * <p>
 * Replaces the legacy {@code boolean} return pattern used by
 * {@code ControlPersonFlow()} and {@code ControlCourseFlow()} in the original
 * {@code DataBase} class. Returning {@code false} on failure required every
 * caller to remember to check the result; throwing an unchecked exception
 * ensures duplicates cannot be silently ignored and maps cleanly to an HTTP
 * {@code 409 Conflict} response.
 */
public class DuplicateEntityException extends RegistryException
{

    /**
     * Constructs a {@code DuplicateEntityException} describing the conflicting entity.
     *
     * @param entityType the Greek display name of the entity type (e.g., {@code "Φοιτητής"}, {@code "Μάθημα"})
     * @param identifier the unique identifier that already exists
     */
    public DuplicateEntityException(String entityType, String identifier)
    {
        super(entityType + " με το αναγνωριστικό '" + identifier + "' υπάρχει ήδη.\n");
    }
}