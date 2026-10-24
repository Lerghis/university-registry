package com.university.registry.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertFalse(ex.getMessage().isBlank());
        assertTrue(ex.getMessage().contains("0"));
    }

    @Test
    void duplicateEntityException_includesEntityTypeAndIdentifier() {
        DuplicateEntityException ex = new DuplicateEntityException("Student", "AM12345");

        assertInstanceOf(RegistryException.class, ex);
        assertTrue(ex.getMessage().contains("Student"));
        assertTrue(ex.getMessage().contains("AM12345"));
    }

    @Test
    void entityNotFoundException_includesEntityTypeAndIdentifier() {
        EntityNotFoundException ex = new EntityNotFoundException("Course", "CS101");

        assertInstanceOf(RegistryException.class, ex);
        assertTrue(ex.getMessage().contains("Course"));
        assertTrue(ex.getMessage().contains("CS101"));
    }
}
