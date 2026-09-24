package com.university.registry.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that each domain exception:
 *   1. Is a RegistryException (so broad catches still work)
 *   2. Produces a clear, useful message
 */
class RegistryExceptionTest {

    @Test
    void invalidSemesterException_isARegistryException_withUsefulMessage() {
        InvalidSemesterException ex = new InvalidSemesterException(0);

        assertInstanceOf(RegistryException.class, ex);
        assertTrue(ex.getMessage().contains("0"));
        assertTrue(ex.getMessage().contains("Semester"));
    }

    @Test
    void duplicateEntityException_includesEntityTypeAndIdentifier() {
        DuplicateEntityException ex = new DuplicateEntityException("Student", "AM12345");

        assertInstanceOf(RegistryException.class, ex);
        assertEquals(
                "Student with identifier 'AM12345' already exists.",
                ex.getMessage()
        );
    }

    @Test
    void entityNotFoundException_includesEntityTypeAndIdentifier() {
        EntityNotFoundException ex = new EntityNotFoundException("Course", "CS101");

        assertInstanceOf(RegistryException.class, ex);
        assertEquals(
                "Course with identifier 'CS101' was not found.",
                ex.getMessage()
        );
    }
}
