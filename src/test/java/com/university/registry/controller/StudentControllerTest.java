package com.university.registry.controller;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.StudentService;
import com.university.registry.web.StudentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Only loads the web configuration needed for StudentController
 * (URL routing, JSON conversion, @Valid checks, and @RestControllerAdvice exception handlers).
 * Does not load @Service, @Repository, or MariaDB.
 */
@WebMvcTest(StudentController.class)
class StudentControllerTest
{
    /*
     * Injects Spring's fake HTTP client (MockMvc)
     *
     * It lets you send fake GET, POST, PUT, and DELETE
     * requests directly into Spring's routing engine
     * in memory—without needing to start a real Tomcat server on port 8080 or use curl
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * Creates a Mockito mock in your service tests AND registers it inside Spring's application context.
     *
     * We use @MockitoBean instead of @Mock here: Because StudentController is managed by Spring
     * (not instantiated manually with new StudentController(...)), Spring looks inside
     * its own context to wire StudentController's constructor. @MockitoBean puts the fake services
     * into Spring's context so Spring plugs them straight into StudentController.
     */
    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private EnrollmentService enrollmentService;


    @Test
    void listStudents_whenHasItems_returns200() throws Exception
    {
        List<Student> students = List.of(new Student("AM1", 1, "Maria", "m@x.gr", "100"),
                                         new Student("AM2", 1, "Eleni", "e@x.gr", "200"));

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listStudents_whenEmpty_returnsAnEmptyList() throws Exception
    {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getStudent_whenExists_returns200AndJson() throws Exception
    {
        when(studentService.getStudentByAm("AM1")).thenReturn(new Student("AM1", 1, "Maria", "m@x.gr", "100"));

        mockMvc.perform(get("/students/AM1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.am").value("AM1"))
                .andExpect(jsonPath("$.name").value("Maria"));
    }

    @Test
    void getStudent_whenMissing_returns404() throws Exception
    {
        when(studentService.getStudentByAm("AM1")).thenThrow(new EntityNotFoundException("Student", "AM1"));

        mockMvc.perform(get("/students/AM1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createStudent_whenCreated_returns201() throws Exception
    {
        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"am":"AM1","semester":1,"name":"Maria","email":"m@x.gr","phone":"100"}
                        """))
                .andExpect(status().isCreated());

        verify(studentService, times(1)).addStudent(any());
    }

    @Test
    void createStudent_whenDuplicate_returns409() throws Exception
    {
        // doThrow(...).when(mock).method(...)
        doThrow(new DuplicateEntityException("Student", "AM1")).when(studentService).addStudent(any());

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"am":"AM1","semester":1,"name":"Maria","email":"m@x.gr","phone":"100"}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void createStudent_withEmptyBody_returns400AndDoesNotCallTheService() throws Exception
    {
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(studentService, never()).addStudent(any());
    }

    @Test
    void updateStudent_whenUpdated_returns200() throws Exception
    {
        mockMvc.perform(put("/students/AM1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"semester":1,"name":"Eleni","email":"e@x.gr","phone":"200"}
                        """))
                .andExpect((status().isOk()));

        verify(studentService, times(1)).updateStudent(any());
    }

    @Test
    void updateStudent_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Student", "AM1")).when(studentService).updateStudent(any());

        mockMvc.perform(put("/students/AM1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"semester":1,"name":"Eleni","email":"e@x.gr","phone":"200"}
                        """))
                .andExpect((status().isNotFound()))
                .andExpect(jsonPath("$.status").value(404));

        verify(studentService, times(1)).updateStudent(any());
    }

    @Test
    void updateStudent_withEmptyBody_returns400AndDoesNotCallTheService() throws Exception
    {
        mockMvc.perform(put("/students/AM1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(studentService, never()).updateStudent(any());
    }

    @Test
    void deleteStudent_whenDeleted_returns204() throws Exception
    {
        mockMvc.perform(delete("/students/AM1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).deleteStudentSafely("AM1");
    }

    @Test
    void deleteStudent_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Student", "AM1")).when(enrollmentService).deleteStudentSafely("AM1");

        mockMvc.perform(delete("/students/AM1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).deleteStudentSafely("AM1");
    }
}
