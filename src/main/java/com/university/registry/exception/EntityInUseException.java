package com.university.registry.exception;

/**
 * Thrown when attempting to delete or unenroll an entity that is still
 * referenced by other records in the system—for example, deleting a
 * {@code Student} with active enrollments, a {@code Course} with assigned
 * professors or students, or unenrolling a student who already has recorded grades.
 * <p>
 * Protects referential integrity at the business-logic layer before reaching
 * the database and typically maps to an HTTP {@code 409 Conflict} response.
 */
public class EntityInUseException extends RegistryException
{
    /**
     * Constructs an {@code EntityInUseException} describing the entity that cannot be removed.
     *
     * @param entityType the Greek display name of the entity type (e.g., {@code "Φοιτητής"}, {@code "Εγγραφή"})
     * @param identifier the unique identifier of the entity still in use
     */
    public EntityInUseException(String entityType, String identifier)
    {
        super(entityType + " με αναγνωριστικό '" + identifier + "' δεν μπορεί να διαγραφεί: εξακολουθεί να αναφέρεται από άλλες εγγραφές.\n");
    }
}