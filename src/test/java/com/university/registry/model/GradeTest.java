package com.university.registry.model;

import com.university.registry.exception.InvalidGradeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class GradeTest
{
    @Test
    void constructor_withNan_throwsInvalidGrade()
    {
        try
        {
            new Grade("AM1", "C1", Float.NaN);
            fail("Expected InvalidGradeException but nothing was thrown.");
        }
        catch (InvalidGradeException ex)
        {

        }
    }

    @Test
    void constructor_withNegativeNumber_throwsInvalidGrade()
    {
        try
        {
            new Grade("AM1", "C1", -0.1f);
            fail("Expected InvalidGradeException but nothing was thrown.");
        }
        catch (InvalidGradeException ex)
        {

        }
    }

    @Test
    void constructor_withGreaterThanTenValue_throwsInvalidGrade()
    {
        try
        {
            new Grade("AM1", "C1", 10.1f);
            fail("Expected InvalidGradeException but nothing was thrown.");
        }
        catch (InvalidGradeException ex)
        {

        }
    }

    @Test
    void constructor_withZero_constructsGrade()
    {
        new Grade("AM1", "C1", 0);
    }

    @Test
    void constructor_withTen_constructsGrade()
    {
        new Grade("AM1", "C1", 10);
    }
}
