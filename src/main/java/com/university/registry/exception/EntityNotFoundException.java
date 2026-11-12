package com.university.registry.exception;

/**
 * Thrown when a lookup for a specific entity fails—for example, searching for
 * a {@code Student} by AM, a {@code Professor} by ID, a {@code Course} by ID,
 * or an enrollment/grade record that does not exist.
 * <p>
 * Replaces the legacy {@code null}-return pattern used by
 * {@code SearchStudByAM()}, {@code SearchProfByID()}, and
 * {@code SearchCourseByID()} in the original {@code DataBase} class.
 * Returning {@code null} forced every caller to perform manual null checks;
 * throwing an unchecked exception prevents {@code NullPointerException} bugs
 * downstream and maps cleanly to an HTTP {@code 404 Not Found} response.
 */
public class EntityNotFoundException extends RegistryException
{
    /**
     * Constructs an {@code EntityNotFoundException} describing the missing entity.
     *
     * @param entityType the Greek display name of the entity type (e.g., {@code "Φοιτητής"}, {@code "Βαθμός"})
     * @param identifier the unique identifier that could not be found
     */
    public EntityNotFoundException(String entityType, String identifier)
    {
        super(entityType + " με το αναγνωριστικό '" + identifier + "' δεν βρέθηκε.\n");
    }
}