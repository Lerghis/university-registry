package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.model.ProfessorCourseId;
import com.university.registry.model.StudentCourseId;
import com.university.registry.model.StudentCourses;
import com.university.registry.repository.ProfessorCoursesRepository;
import com.university.registry.repository.StudentCoursesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.text.html.parser.Entity;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest
{
    private StudentService studentService;
    private ProfessorService professorService;
    private CourseService courseService;
    private StudentCoursesRepository studentCoursesRepository;
    private ProfessorCoursesRepository professorCoursesRepository;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setup()
    {
        studentService = mock(StudentService.class);
        professorService = mock(ProfessorService.class);
        courseService = mock(CourseService.class);
        studentCoursesRepository = mock(StudentCoursesRepository.class);
        professorCoursesRepository = mock(ProfessorCoursesRepository.class);

        enrollmentService = new EnrollmentService(studentService, professorService, courseService, studentCoursesRepository, professorCoursesRepository);
    }

    @Test
    void enrollStudentToCourse_whenAlreadyEnrolled_throwsDuplicateAndSavesNothing()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(true);

        try
        {
            enrollmentService.enrollStudentToCourse("AM1", "C1");
            fail("Expected DuplicateEntityException but nothing was thrown.");
        }
        catch (DuplicateEntityException ex)
        {
            verify(studentCoursesRepository, never()).save(any());
        }
    }

    @Test
    void enrollStudentToCourse_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            enrollmentService.enrollStudentToCourse("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).save(any());
        }
    }

    @Test
    void enrollStudentToCourse_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        try
        {
            enrollmentService.enrollStudentToCourse("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).save(any());
        }
    }

    @Test
    void enrollStudentToCourse_whenNotEnrolled_savesTheRightEnrollment()
    {
        enrollmentService.enrollStudentToCourse("AM1", "C1");

        ArgumentCaptor<StudentCourses> captor = ArgumentCaptor.forClass(StudentCourses.class);

        verify(studentCoursesRepository).save(captor.capture());

        assertEquals("AM1", captor.getValue().getStudentAm());
        assertEquals("C1", captor.getValue().getCourseId());
    }

    @Test
    void unenrollStudent_whenNotEnrolled_throwsEntityNotFound()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(false);

        try
        {
            enrollmentService.unenrollStudent("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unenrollStudent_whenHasAGrade_throwsEntityInUse()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(true);
        when(studentCoursesRepository.countGradesFor("AM1", "C1")).thenReturn(1L);

        try
        {
            enrollmentService.unenrollStudent("AM1", "C1");
            fail("Expected EntityInUseException but nothing was thrown.");
        }
        catch (EntityInUseException ex)
        {
            verify(studentCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unenrollStudent_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            enrollmentService.unenrollStudent("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unenrollStudent_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "P1"));

        try
        {
            enrollmentService.unenrollStudent("AM1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unenrollStudent_whenEnrolled_deletesTheRightKey()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(true);

        enrollmentService.unenrollStudent("AM1", "C1");

        verify(studentCoursesRepository, times(1)).deleteById(new StudentCourseId("AM1", "C1"));
    }

    @Test
    void getCoursesForStudent_whenStudentMissing_throwsEntityNotFound()
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        try
        {
            enrollmentService.getCoursesForStudent("AM1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(studentCoursesRepository, never()).findCoursesForStudent(any());
        }
    }

    @Test
    void getCoursesForStudent_returnsRepositoryList()
    {
        List<Course> courses = List.of(new Course("C1", "C Programming", 1));

        when(studentCoursesRepository.findCoursesForStudent("AM1")).thenReturn(courses);

        assertEquals(courses, enrollmentService.getCoursesForStudent("AM1"));
    }

    @Test
    void getCoursesForProfessor_whenProfessorMissing_throwsEntityNotFound()
    {
        when(professorService.getProfById("P1")).thenThrow(new EntityNotFoundException("Professor", "P1"));

        try
        {
            enrollmentService.getCoursesForProfessor("P1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).findCoursesForProfessor(any());
        }
    }

    @Test
    void getCoursesForProfessor_returnsRepositoryList()
    {
        List<Course> courses = List.of(new Course("C1", "C Programming", 1));

        when(professorCoursesRepository.findCoursesForProfessor("P1")).thenReturn(courses);

        assertEquals(courses, enrollmentService.getCoursesForProfessor("P1"));
    }

    @Test
    void assignCourseToProfessor_whenAlreadyAssigned_throwsDuplicateAndSavesNothing()
    {
        when(professorCoursesRepository.existsById(new ProfessorCourseId("P1", "C1"))).thenReturn(true);

        try
        {
            enrollmentService.assignCourseToProfessor("P1", "C1");
            fail("Expected DuplicateEntityException but nothing was thrown.");
        }
        catch (DuplicateEntityException ex)
        {
            verify(professorCoursesRepository, never()).save(any());
        }
    }

    @Test
    void assignCourseToProfessor_whenProfessorMissing_throwsEntityNotFound()
    {
        when(professorService.getProfById("P1")).thenThrow(new EntityNotFoundException("Professor", "P1"));

        try
        {
            enrollmentService.assignCourseToProfessor("P1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).save(any());
        }
    }

    @Test
    void assignCourseToProfessor_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        try
        {
            enrollmentService.assignCourseToProfessor("P1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).save(any());
        }
    }

    @Test
    void assignCourseToProfessor_whenNotAssigned_savesAssignment()
    {
        when(professorCoursesRepository.existsById(new ProfessorCourseId("P1", "C1"))).thenReturn(false);

        enrollmentService.assignCourseToProfessor("P1", "C1");

        verify(professorCoursesRepository, times(1)).save(any());
    }

    @Test
    void unassignProfessor_whenNotAssigned_throwsEntityNotFound()
    {
        when(professorCoursesRepository.existsById(new ProfessorCourseId("P1", "C1"))).thenReturn(false);

        try
        {
            enrollmentService.unassignProfessor("P1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unassignProfessor_whenProfessorMissing_throwsEntityNotFound()
    {
        when(professorService.getProfById("P1")).thenThrow(new EntityNotFoundException("Professor", "P1"));

        try
        {
            enrollmentService.unassignProfessor("P1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unassignProfessor_whenCourseMissing_throwsEntityNotFound()
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        try
        {
            enrollmentService.unassignProfessor("P1", "C1");
            fail("Expected EntityNotFoundException but nothing was thrown.");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorCoursesRepository, never()).deleteById(any());
        }
    }

    @Test
    void unassignProfessor_whenAssigned_unassigns()
    {
        when(professorCoursesRepository.existsById(new ProfessorCourseId("P1", "C1"))).thenReturn(true);

        enrollmentService.unassignProfessor("P1", "C1");

        verify(professorCoursesRepository, times(1)).deleteById(any());
    }

    @Test
    void deleteStudentSafely_whenEnrolled_throwsEntityInUse()
    {
        when(studentCoursesRepository.existsByStudentAm("AM1")).thenReturn(true);

        try
        {
            enrollmentService.deleteStudentSafely("AM1");
            fail("Expected EntityInUseException but nothing was thrown.");
        }
        catch (EntityInUseException ex)
        {
            verify(studentService, never()).deleteStudent(any());
        }
    }

    @Test
    void deleteStudentSafely_whenNotEnrolled_deletesStudent()
    {
        when(studentCoursesRepository.existsByStudentAm("AM1")).thenReturn(false);

        enrollmentService.deleteStudentSafely("AM1");

        verify(studentService, times(1)).deleteStudent("AM1");
    }

    @Test
    void deleteProfessorSafely_whenAssigned_throwsEntityInUse()
    {
        when(professorCoursesRepository.existsByProfId("P1")).thenReturn(true);

        try
        {
            enrollmentService.deleteProfessorSafely("P1");
            fail("Expected EntityInUseException but nothing was thrown.");
        }
        catch (EntityInUseException ex)
        {
            verify(professorService, never()).deleteProfessor(any());
        }
    }

    @Test
    void deleteProfessorSafely_whenNotAssigned_deletesProfessor()
    {
        when(professorCoursesRepository.existsByProfId("P1")).thenReturn(false);

        enrollmentService.deleteProfessorSafely("P1");

        verify(professorService, times(1)).deleteProfessor("P1");
    }

    @Test
    void deleteCourseSafely_whenEnrolled_throwsEntityInUse()
    {
        when(studentCoursesRepository.existsByCourseId("C1")).thenReturn(true);

        try
        {
            enrollmentService.deleteCourseSafely("C1");
            fail("Expected EntityInUseException but nothing was thrown.");
        }
        catch (EntityInUseException ex)
        {
            verify(courseService, never()).deleteCourse(any());
        }
    }

    @Test
    void deleteCourseSafely_whenAssigned_throwsEntityInUse()
    {
        when(professorCoursesRepository.existsByCourseId("C1")).thenReturn(true);

        try
        {
            enrollmentService.deleteCourseSafely("C1");
            fail("Expected EntityInUseException but nothing was thrown.");
        }
        catch (EntityInUseException ex)
        {
            verify(courseService, never()).deleteCourse(any());
        }
    }

    @Test
    void deleteCourseSafely_whenNotInUse_doDelete()
    {
        when(studentCoursesRepository.existsByCourseId("C1")).thenReturn(false);
        when(professorCoursesRepository.existsByCourseId("C1")).thenReturn(false);

        enrollmentService.deleteCourseSafely("C1");

        verify(courseService, times(1)).deleteCourse("C1");
    }

    @Test
    void isStudentEnrolled_whenEnrollmentExists_returnsTrue()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(true);

        boolean result = enrollmentService.isStudentEnrolled("AM1", "C1");

        assertTrue(result);
        verify(studentCoursesRepository, times(1)).existsById(new StudentCourseId("AM1", "C1"));
    }

    @Test
    void isStudentEnrolled_whenEnrollmentMissing_returnsFalse()
    {
        when(studentCoursesRepository.existsById(new StudentCourseId("AM1", "C1"))).thenReturn(false);

        boolean result = enrollmentService.isStudentEnrolled("AM1", "C1");

        assertFalse(result);
        verify(studentCoursesRepository, times(1)).existsById(new StudentCourseId("AM1", "C1"));
    }
}
