package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidGradeException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Grade;
import com.university.registry.model.StudentCourseId;
import com.university.registry.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeServiceTest
{
    private StudentService studentService;
    private CourseService courseService;
    private EnrollmentService enrollmentService;
    private GradeService gradeService;
    private GradeRepository gradeRepository;

    @BeforeEach
    void setup()
    {
        studentService = mock(StudentService.class);
        courseService = mock(CourseService.class);
        enrollmentService = mock(EnrollmentService.class);
        gradeRepository = mock(GradeRepository.class);

        gradeService = new GradeService(studentService, courseService, enrollmentService, gradeRepository);
    }

    @Test
    void recordGrade_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            gradeService.recordGrade("AM1", "C1", 8.5f);
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void recordGrade_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        try
        {
            gradeService.recordGrade("AM1", "C1", 8.5f);
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void recordGrade_whenStudentNotEnrolled_throwsEntityNotFound()
    {
        when(enrollmentService.isStudentEnrolled("AM1", "C1")).thenReturn(false);

        try
        {
            gradeService.recordGrade("AM1", "C1", 8.5f);
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void recordGrade_whenDuplicate_throwsDuplicateAndSavesNothing()
    {
        when(enrollmentService.isStudentEnrolled("AM1", "C1")).thenReturn(true);
        when(gradeRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(true);

        try
        {
            gradeService.recordGrade("AM1", "C1", 8.5f);
            fail("Expected DuplicateEntityException but nothing was thrown.");
        }
        catch (DuplicateEntityException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void recordGrade_whenInvalidValue_throwsInvalidGrade()
    {
        when(enrollmentService.isStudentEnrolled("AM1", "C1")).thenReturn(true);
        when(gradeRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(false);

        try
        {
            gradeService.recordGrade("AM1", "C1", 15f);
            fail("Expected InvalidGradeException but nothing was thrown.");
        }
        catch (InvalidGradeException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void recordGrade_whenEnrolledAndNew_savesTheRightGrade()
    {
        when(enrollmentService.isStudentEnrolled("AM1", "C1")).thenReturn(true);

        gradeService.recordGrade("AM1", "C1", 8.5f);

        ArgumentCaptor<Grade> captor = ArgumentCaptor.forClass(Grade.class);
        verify(gradeRepository).save(captor.capture());

        assertEquals("AM1", captor.getValue().getStudentAm());
        assertEquals("C1", captor.getValue().getCourseId());
        assertEquals(8.5f, captor.getValue().getGradeValue());
    }

    @Test
    void getGrade_whenGradeExists_returnsGrade()
    {
        Grade grade = new Grade("AM1", "C1", 8.5f);
        StudentCourseId id = new StudentCourseId("AM1", "C1");

        when(gradeRepository.findById(id)).thenReturn(Optional.of(grade));

        gradeService.getGrade("AM1", "C1");

        assertEquals(8.5f, grade.getGradeValue(), 0.0001f);

        verify(gradeRepository, times(1)).findById(id);
    }

    @Test
    void getGrade_whenGradeMissing_throwsEntityNotFound()
    {
        StudentCourseId id = new StudentCourseId("AM1", "C1");

        when(gradeRepository.findById(id)).thenReturn(Optional.empty());

        try
        {
            gradeService.getGrade("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, times(1)).findById(id);
        }
    }

    @Test
    void getGradesForStudent_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            gradeService.getGradesForStudent("AM1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).findByStudentAm(any());
        }
    }

    @Test
    void getGradesForStudent_whenNone_returnsAnEmptyCollection()
    {
        when(gradeRepository.findByStudentAm("AM1")).thenReturn(List.of());

        boolean isEmpty = gradeService.getGradesForStudent("AM1").isEmpty();

        assertTrue(isEmpty);

        verify(gradeRepository, times(1)).findByStudentAm("AM1");
    }

    @Test
    void getGradesForStudent_whenExist_returnsTheCollectionWithGrades()
    {
        List<Grade> grades = List.of(new Grade("AM1", "C1", 8.5f));

        when(gradeRepository.findByStudentAm("AM1")).thenReturn(grades);

        assertEquals(grades, gradeService.getGradesForStudent("AM1"));
    }

    @Test
    void getStudentAverage_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            gradeService.getStudentAverage("AM1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).findAverageByStudentAm(any());
        }
    }

    @Test
    void getStudentAverage_whenNoGrades_throwsNoGradesRecorded()
    {
        when(gradeRepository.existsByStudentAm("AM1")).thenReturn(false);

        try
        {
            gradeService.getStudentAverage("AM1");
            fail("Expected NoGradesRecordedException but nothing was thrown.");
        }
        catch (NoGradesRecordedException ex)
        {
            verify(gradeRepository, never()).findAverageByStudentAm(any());
        }
    }

    @Test
    void getStudentAverage_whenGradesExist_returnsAverageValue()
    {
        when(gradeRepository.existsByStudentAm("AM1")).thenReturn(true);
        when(gradeRepository.findAverageByStudentAm("AM1")).thenReturn(8.5);

        float result = gradeService.getStudentAverage("AM1");

        assertEquals(8.5f, result, 0.001f);

        verify(gradeRepository, times(1)).findAverageByStudentAm(any());
    }

    @Test
    void getCourseAverage_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        try
        {
            gradeService.getCourseAverage("C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).findAverageByCourseId(any());
        }
    }

    @Test
    void getCourseAverage_whenNoGrades_throwsNoGradesRecorded()
    {
        when(gradeRepository.existsByCourseId("C1")).thenReturn(false);

        try
        {
            gradeService.getCourseAverage("C1");
            fail("Expected NoGradesRecordedException but nothing was thrown.");
        }
        catch (NoGradesRecordedException ex)
        {
            verify(gradeRepository, never()).findAverageByCourseId(any());
        }
    }

    @Test
    void getCourseAverage_whenGradesExist_returnsAverageValue()
    {
        when(gradeRepository.existsByCourseId("C1")).thenReturn(true);
        when(gradeRepository.findAverageByCourseId("C1")).thenReturn(8.5);

        float result = gradeService.getCourseAverage("C1");

        assertEquals(8.5f, result, 0.001f);

        verify(gradeRepository, times(1)).findAverageByCourseId(any());
    }

    @Test
    void updateGrade_whenMissing_throwsEntityNotFound()
    {
        when(gradeRepository.findById(new StudentCourseId("AM1", "C1"))).thenReturn(Optional.empty());

        try
        {
            gradeService.updateGrade(new Grade("AM1", "C1", 10));
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).save(any());
        }
    }

    @Test
    void updateGrade_whenExists_changesTheValue()
    {
        Grade existingGrade = new Grade("AM1", "C1", 8.5f);
        Grade newInfo = new Grade("AM1", "C1", 10);

        when(gradeRepository.findById(new StudentCourseId("AM1", "C1"))).thenReturn(Optional.of(existingGrade));

        gradeService.updateGrade(newInfo);

        verify(gradeRepository, times(1)).save(newInfo);
    }

    @Test
    void deleteGrade_whenMissing_throwsEntityNotFound()
    {
        when(gradeRepository.findById(new StudentCourseId("AM1", "C1"))).thenReturn(Optional.empty());

        try
        {
            gradeService.deleteGrade("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(gradeRepository, never()).deleteById(any());
        }
    }

    @Test
    void deleteGrade_whenExists_removesIt()
    {
        Grade existingGrade = new Grade("AM1", "C1", 8.5f);

        when(gradeRepository.findById(new StudentCourseId("AM1", "C1"))).thenReturn(Optional.of(existingGrade));

        gradeService.deleteGrade("AM1", "C1");

        verify(gradeRepository, times(1)).deleteById(new StudentCourseId("AM1", "C1"));
    }
}
