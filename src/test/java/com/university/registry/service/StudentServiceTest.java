package com.university.registry.service;

import com.university.registry.dao.InMemoryStudentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest
{
    private StudentService service;

    @BeforeEach
    void setup()
    {
        service = new StudentService(new InMemoryStudentDAO());
    }

    @Test
    void addStudent_withNewAm_canBeFoundAfterwards() throws Exception
    {
        Student student = new Student("AM1", 1, "Maria", "m@x.gr", "100");

        service.addStudent(student);

        assertEquals("Maria", service.getStudentByAm("AM1").getName());
    }

    @Test
    void addStudent_withExistingAm_throwsDuplicateEntityException() throws Exception
    {
        service.addStudent(new Student("AM1", 1, "Maria", "m@x.gr", "100"));

        try
        {
            service.addStudent(new Student("AM1", 2, "Nikos", "n@x.gr", "200"));
            fail("Expected a DuplicateEntityException but nothing was thrown");
        }
        catch (DuplicateEntityException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void getStudentByAm_whenMissing_throwsEntityNotFoundException()
    {
        try
        {
            service.getStudentByAm("AM1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void updateStudent_whenMissing_throwsEntityNotFoundException() throws Exception
    {
        Student student = new Student("AM1", 1, "Maria", "m@x.gr", "100");

        try
        {
            service.updateStudent(student);
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void updateStudent_whenExists_changesTheStoredValues() throws Exception
    {
        service.addStudent(new Student("AM1", 1, "Maria", "m@x.gr", "100"));

        service.updateStudent(new Student("AM1", 1, "Eleni", "e@x.gr", "200"));   // a DIFFERENT object, same AM

        assertEquals("Eleni", service.getStudentByAm("AM1").getName());
    }

    @Test
    void deleteStudent_whenMissing_throwsEntityNotFoundException()
    {
        try
        {
            service.deleteStudent("AM1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void deleteStudent_whenExists_removesIt() throws Exception
    {
        service.addStudent(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        service.deleteStudent("AM1");

        try
        {
            service.getStudentByAm("AM1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void getAllStudents_whenEmpty_returnsAnEmptyCollection()
    {
        assertTrue(service.getAllStudents().isEmpty());
    }
}
