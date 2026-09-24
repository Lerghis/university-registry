package com.university.registry.exception;

/**
 * Thrown when a lookup for a specific entity fails - for example, looking
 * up a Student by AM, a Professor by PID, or a Course by CID that does not
 * exist.
 * <p>
 * This replaces the {@code null}-return pattern used by
 * {@code SearchStudByAM()}, {@code SearchProfByID()}, and
 * {@code SearchCourseByID()} in the original {@code DataBase} class.
 * Returning {@code null} on a miss forces every caller to remember a null
 * check (your original code did this manually with
 * {@code if (student == null) ...} before most operations); throwing an
 * exception instead means a missing entity can never silently propagate
 * as a {@code NullPointerException} several calls later.
 */
public class EntityNotFoundException extends RegistryException
{

    public EntityNotFoundException(String entityType, String identifier)
    {
        super(entityType + " με το αναγνωριστικό '" + identifier + "' δεν βρέθηκε.\n");
    }
}
