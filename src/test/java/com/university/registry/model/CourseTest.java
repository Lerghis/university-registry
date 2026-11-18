package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class CourseTest
{
    @Test
    void constructor_withLessThanOne_throwsInvalidSemester()
    {
        try
        {
            new Course("C1", "Test Course", 0);
            fail("Expected InvalidSemesterException but nothing was thrown.");
        }
        catch (InvalidSemesterException ex)
        {

        }
    }

    @Test
    void constructor_withValidSemesterValue_constructsCourse()
    {
        new Course("C1", "Test Course", 1);
    }
}
