package com.university.registry.exception;

/**
 * Thrown when attempting to calculate a grade average for a {@code Student}
 * or a {@code Course} that currently has no recorded grades.
 * <p>
 * Prevents division-by-zero errors or misleading {@code 0.0} averages when
 * no grade data exists yet, and typically maps to an HTTP {@code 404 Not Found}
 * or {@code 409 Conflict} response depending on API semantics.
 */
public class NoGradesRecordedException extends RegistryException
{
    /**
     * Constructs a {@code NoGradesRecordedException} for the entity lacking grade records.
     *
     * @param entityType the Greek display name of the entity type (e.g., {@code "Φοιτητής"}, {@code "Μάθημα"})
     * @param identifier the unique identifier of the student or course
     */
    public NoGradesRecordedException(String entityType, String identifier)
    {
        super(entityType + " με το αναγνωριστικό " + identifier + " δεν έχει καταχωρημένους βαθμούς ακόμη.\n");
    }
}