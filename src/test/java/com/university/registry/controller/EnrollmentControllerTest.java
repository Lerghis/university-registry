package com.university.registry.controller;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.service.EnrollmentService;
import com.university.registry.web.EnrollmentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void enrollStudent_whenEnrolled_returns201() throws Exception
    {
        mockMvc.perform(post("/students/AM1/courses/C1"))
                .andExpect(status().isCreated());

        verify(enrollmentService, times(1)).enrollStudentToCourse("AM1", "C1");
    }

    @Test
    void enrollStudent_whenUnknownCourse_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Course", "C1")).when(enrollmentService).enrollStudentToCourse("AM1", "C1");

        mockMvc.perform(post("/students/AM1/courses/C1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).enrollStudentToCourse("AM1", "C1");
    }

    @Test
    void enrollStudent_whenDuplicate_returns409() throws Exception
    {
        doThrow(new DuplicateEntityException("Enrollment", "AM1" + "-" + "C1")).when(enrollmentService).enrollStudentToCourse("AM1", "C1");

        mockMvc.perform(post("/students/AM1/courses/C1"))
                .andExpect(status().isConflict());

        verify(enrollmentService, times(1)).enrollStudentToCourse("AM1", "C1");
    }

    @Test
    void unenrollStudent_whenUnenrolls_returns204() throws Exception
    {
        mockMvc.perform(delete("/students/AM1/courses/C1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).unenrollStudent("AM1", "C1");
    }

    @Test
    void unenrollStudent_whenEnrollmentMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Enrollment", "AM1" + "-" + "C1")).when(enrollmentService).unenrollStudent("AM1", "C1");

        mockMvc.perform(delete("/students/AM1/courses/C1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).unenrollStudent("AM1", "C1");
    }

    @Test
    void unenrollStudent_whenGradedEnrollment_returns409() throws Exception
    {
        doThrow(new EntityInUseException("Enrollment", "AM1" + "-" + "C1")).when(enrollmentService).unenrollStudent("AM1", "C1");

        mockMvc.perform(delete("/students/AM1/courses/C1"))
                .andExpect(status().isConflict());

        verify(enrollmentService, times(1)).unenrollStudent("AM1", "C1");
    }

    @Test
    void listStudentCourses_whenHasItems_returns200() throws Exception
    {
        List<Course> studentCourses = List.of(new Course("C1", "C Programming", 1), new Course("C2", "Databases", 1));

        when(enrollmentService.getCoursesForStudent("AM1")).thenReturn(studentCourses);

        mockMvc.perform(get("/students/AM1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(enrollmentService, times(1)).getCoursesForStudent("AM1");
    }

    @Test
    void listStudentCourses_whenEmpty_returns200() throws Exception
    {
        when(enrollmentService.getCoursesForStudent("AM1")).thenReturn(List.of());

        mockMvc.perform(get("/students/AM1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(enrollmentService, times(1)).getCoursesForStudent("AM1");
    }

    @Test
    void listStudentCourses_whenStudentMissing_returns404() throws Exception
    {
        when(enrollmentService.getCoursesForStudent("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        mockMvc.perform(get("/students/AM1/courses"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).getCoursesForStudent("AM1");
    }

    @Test
    void assignProfessor_whenAssigned_returns201() throws Exception
    {
        mockMvc.perform(post("/professors/P1/courses/C1"))
                .andExpect(status().isCreated());

        verify(enrollmentService, times(1)).assignCourseToProfessor("P1", "C1");
    }

    @Test
    void assignProfessor_whenUnknownCourse_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Course", "C1")).when(enrollmentService).assignCourseToProfessor("P1", "C1");

        mockMvc.perform(post("/professors/P1/courses/C1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).assignCourseToProfessor("P1", "C1");
    }

    @Test
    void assignProfessor_whenDuplicate_returns409() throws Exception
    {
        doThrow(new DuplicateEntityException("Assignment", "P1" + "-" + "C1")).when(enrollmentService).assignCourseToProfessor("P1", "C1");

        mockMvc.perform(post("/professors/P1/courses/C1"))
                .andExpect(status().isConflict());

        verify(enrollmentService, times(1)).assignCourseToProfessor("P1", "C1");
    }

    @Test
    void unassignProfessor_whenUnassigns_returns204() throws Exception
    {
        mockMvc.perform(delete("/professors/P1/courses/C1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).unassignProfessor("P1", "C1");
    }

    @Test
    void unassignProfessor_whenAssignmentMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Assignment", "P1" + "-" + "C1")).when(enrollmentService).unassignProfessor("P1", "C1");

        mockMvc.perform(delete("/professors/P1/courses/C1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).unassignProfessor("P1", "C1");
    }

    @Test
    void listProfessorCourses_whenHasItems_returns200() throws Exception
    {
        List<Course> professorCourses = List.of(new Course("C1", "C Programming", 1), new Course("C2", "Databases", 1));

        when(enrollmentService.getCoursesForProfessor("P1")).thenReturn(professorCourses);

        mockMvc.perform(get("/professors/P1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(enrollmentService, times(1)).getCoursesForProfessor("P1");
    }

    @Test
    void listProfessorCourses_whenEmpty_returns200() throws Exception
    {
        when(enrollmentService.getCoursesForProfessor("P1")).thenReturn(List.of());

        mockMvc.perform(get("/professors/P1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(enrollmentService, times(1)).getCoursesForProfessor("P1");
    }

    @Test
    void listProfessorCourses_whenProfessortMissing_returns404() throws Exception
    {
        when(enrollmentService.getCoursesForProfessor("P1")).thenThrow(new EntityNotFoundException("Professor", "P1"));

        mockMvc.perform(get("/professors/P1/courses"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).getCoursesForProfessor("P1");
    }
}
