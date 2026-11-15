package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.model.Professor;
import com.university.registry.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class CourseServiceTest
{
    private CourseRepository courseRepository;
    private CourseService courseService;

    @BeforeEach
    void setup()
    {
        courseRepository = mock(CourseRepository.class);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void addCourse_withNewCourseId_savesCourse() throws Exception
    {
        Course newCourse = new Course("C1", "C Programming", 1);
        when(courseRepository.existsById("C1")).thenReturn(false);

        courseService.addCourse(newCourse);

        verify(courseRepository, times(1)).save(newCourse);
    }

    @Test
    void addCourse_withExistingId_throwsDuplicateEntityException() throws Exception
    {
        when(courseRepository.existsById("C1")).thenReturn(true); // program the mock

        try
        {
            courseService.addCourse(new Course("C1", "Databases", 1));
            fail("Expected a DuplicateEntityException but nothing was thrown");
        }
        catch (DuplicateEntityException ex)
        {
            verify(courseRepository, never()).save(any()); // nothing may be saved
        }
    }

    @Test
    void getCourseId_whenMissing_throwsEntityNotFoundException()
    {
        when(courseRepository.findById("C1")).thenReturn(Optional.empty());

        try
        {
            courseService.getCourseById("C1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void updateCourse_whenMissing_throwsEntityNotFoundException() throws Exception
    {
        when(courseRepository.findById("C1")).thenReturn(Optional.empty());

        try
        {
            courseService.updateCourse(new Course("C1", "C Programming", 1));
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(courseRepository, never()).save(any());
        }
    }

    @Test
    void updateCourse_whenExists_changesTheStoredValues() throws Exception
    {
        Course existingCourse = new Course("C1", "C Programming", 1);
        Course newInfo = new Course("C1", "Databases", 1);

        // Tells the mock repo that "AM1" exists so getStudentByAm() doesn't throw an exception
        when(courseRepository.findById("C1")).thenReturn(Optional.of(existingCourse));

        courseService.updateCourse(newInfo);

        // VERIFY: Prove that save() was called specifically with 'newInfo'
        verify(courseRepository, times(1)).save(newInfo);
    }

    @Test
    void deleteCourse_whenMissing_throwsEntityNotFoundException() throws Exception
    {
        when(courseRepository.findById("C1")).thenReturn(Optional.empty());

        try
        {
            courseService.deleteCourse("C1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(courseRepository, never()).deleteById(any());
        }
    }

    @Test
    void deleteCourse_whenExists_removesIt() throws Exception
    {
        Course existingCourse = new Course("C1", "C Programming", 1);
        when(courseRepository.findById("C1")).thenReturn(Optional.of(existingCourse));

        courseService.deleteCourse("C1");

        verify(courseRepository, times(1)).deleteById("C1");
    }

    @Test
    void getAllCourses_whenEmpty_returnsAnEmptyCollection()
    {
        when(courseRepository.findAll()).thenReturn(List.of());

        boolean isEmpty = courseService.getAllCourses().isEmpty();

        assertTrue(isEmpty);

        verify(courseRepository, times(1)).findAll();
    }
}
