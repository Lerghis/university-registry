package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;
import com.university.registry.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest
{
    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setup()
    {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void addStudent_withNewAm_savesStudent() throws Exception
    {
        Student newStudent = new Student("AM1", 1, "Maria", "m@x.gr", "100");
        when(studentRepository.existsById("AM1")).thenReturn(false);

        studentService.addStudent(newStudent);

        verify(studentRepository, times(1)).save(newStudent);
    }

    @Test
    void addStudent_withExistingAm_throwsDuplicateEntityException() throws Exception
    {
        when(studentRepository.existsById("AM1")).thenReturn(true); // program the mock

        try
        {
            studentService.addStudent(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
            fail("Expected a DuplicateEntityException but nothing was thrown");
        }
        catch (DuplicateEntityException ex)
        {
            verify(studentRepository, never()).save(any()); // nothing may be saved
        }
    }

    @Test
    void getStudentByAm_whenMissing_throwsEntityNotFoundException()
    {
        when(studentRepository.findById("AM1")).thenReturn(Optional.empty());

        try
        {
            studentService.getStudentByAm("AM1");
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
        when(studentRepository.findById("AM1")).thenReturn(Optional.empty());

        try
        {
            studentService.updateStudent(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentRepository, never()).save(any());
        }
    }

    @Test
    void updateStudent_whenExists_changesTheStoredValues() throws Exception
    {
        Student existingStudent = new Student("AM1", 1, "Maria", "m@x.gr", "100");
        Student newInfo = new Student("AM1", 1, "Eleni", "e@x.gr", "200");

        // Tells the mock repo that "AM1" exists so getStudentByAm() doesn't throw an exception
        when(studentRepository.findById("AM1")).thenReturn(Optional.of(existingStudent));

        studentService.updateStudent(newInfo);

        // VERIFY: Prove that save() was called specifically with 'newInfo'
        verify(studentRepository, times(1)).save(newInfo);
    }

    @Test
    void deleteStudent_whenMissing_throwsEntityNotFoundException() throws Exception
    {
        when(studentRepository.findById("AM1")).thenReturn(Optional.empty());

        try
        {
            studentService.deleteStudent("AM1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentRepository, never()).deleteById(any());
        }
    }

    @Test
    void deleteStudent_whenExists_removesIt() throws Exception
    {
        Student existingStudent = new Student("AM1", 1, "Maria", "m@x.gr", "100");
        when(studentRepository.findById("AM1")).thenReturn(Optional.of(existingStudent));

        studentService.deleteStudent("AM1");

        verify(studentRepository, times(1)).deleteById("AM1");
    }

    @Test
    void getAllStudents_whenEmpty_returnsAnEmptyCollection()
    {
        when(studentRepository.findAll()).thenReturn(List.of());

        boolean isEmpty = studentService.getAllStudents().isEmpty();

        assertTrue(isEmpty);

        verify(studentRepository, times(1)).findAll();
    }
}
