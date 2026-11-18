package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class StudentTest
{
    @Test
    void constructor_withLessThanOne_throwsInvalidSemester()
    {
        try
        {
            new Student("AM1", 0, "TestName", "t@x.gr", "100");
            fail("Expected InvalidSemesterException but nothing was thrown.");
        }
        catch (InvalidSemesterException ex)
        {

        }
    }

    @Test
    void constructor_withValidSemesterValue_constructsStudent()
    {
        new Student("AM1", 1, "TestName", "t@x.gr", "100");
    }
}
