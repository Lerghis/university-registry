package com.university.registry.controller;

import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidGradeException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Grade;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.GradeService;
import com.university.registry.web.GradeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradeController.class)
public class GradeControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradeService gradeService;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void listGrades_whenHasItems_returns200() throws Exception
    {
        List<Grade> grades = List.of(new Grade("AM1", "C1", 7.2f));

        when(gradeService.getGradesForStudent("AM1")).thenReturn(grades);

        mockMvc.perform(get("/students/AM1/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listGrades_whenEmpty_returnsAnEMptyList() throws Exception
    {
        when(gradeService.getGradesForStudent("AM1")).thenReturn(List.of());

        mockMvc.perform(get("/students/AM1/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addGrade_whenAdded_returns201() throws Exception
    {
        mockMvc.perform(post("/students/AM1/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","value":7.2}
                        """))
                .andExpect(status().isCreated());

        verify(gradeService, times(1)).recordGrade("AM1", "C1", 7.2f);
    }

    @Test
    void addGrade_whenEnrollmentMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Enrollment", "AM1" + "-" + "C1")).when(gradeService).recordGrade("AM1", "C1", 7.2f);

        mockMvc.perform(post("/students/AM1/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","value":7.2}
                        """))
                .andExpect(status().isNotFound());

        verify(gradeService, times(1)).recordGrade("AM1", "C1", 7.2f);
    }

    @Test
    void addGrade_whenValueMissing_returns400() throws Exception
    {
        mockMvc.perform(post("/students/AM1/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1"}
                        """))
                .andExpect(status().isBadRequest());

        verify(gradeService, never()).recordGrade(any(), any(), anyFloat());
    }

    @Test
    void addGrade_whenInvalidGradeValue_returns400() throws Exception
    {
        doThrow(new InvalidGradeException(15)).when(gradeService).recordGrade("AM1", "C1", 15);

        mockMvc.perform(post("/students/AM1/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1", "value":15}
                        """))
                .andExpect(status().isBadRequest());

        verify(gradeService, times(1)).recordGrade("AM1", "C1", 15);
    }

    @Test
    void updateGrade_whenExists_returns200() throws Exception
    {
        mockMvc.perform(put("/students/AM1/grades/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"value":8}
                        """))
                .andExpect(status().isOk());

        verify(gradeService, times(1)).updateGrade(any());
    }

    @Test
    void updateGrade_whenInvalidGradeValue_returns400() throws Exception
    {
        doThrow(new InvalidGradeException(15)).when(gradeService).updateGrade(any());

        mockMvc.perform(put("/students/AM1/grades/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"value":15}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateGrade_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Grade", "AM1" + "-" + "C1")).when(gradeService).updateGrade(any());

        mockMvc.perform(put("/students/AM1/grades/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"value":8}
                        """))
                .andExpect(status().isNotFound());

        verify(gradeService, times(1)).updateGrade(any());
    }

    @Test
    void deleteGrade_whenDeleted_returns204() throws Exception
    {
        mockMvc.perform(delete("/students/AM1/grades/C1"))
                .andExpect(status().isNoContent());

        verify(gradeService, times(1)).deleteGrade("AM1", "C1");
    }

    @Test
    void deleteGrade_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Grade", "AM1" + "-" + "C1")).when(gradeService).deleteGrade("AM1", "C1");

        mockMvc.perform(delete("/students/AM1/grades/C1"))
                .andExpect(status().isNotFound());

        verify(gradeService, times(1)).deleteGrade("AM1", "C1");
    }

    @Test
    void getStudentAverage_whenGradesExist_returns200() throws Exception
    {
        when(gradeService.getStudentAverage("AM1")).thenReturn(8.5f);

        mockMvc.perform(get("/students/AM1/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("8.5"));

        verify(gradeService, times(1)).getStudentAverage("AM1");
    }

    @Test
    void getStudentAverage_whenNoGrades_returns404() throws Exception
    {
        when(gradeService.getStudentAverage("AM1")).thenThrow(new NoGradesRecordedException("Student", "AM1"));

        mockMvc.perform(get("/students/AM1/average"))
                .andExpect(status().isNotFound());

        verify(gradeService, times(1)).getStudentAverage("AM1");
    }

    @Test
    void getCourseAverage_whenGradesExist_returns200() throws Exception
    {
        when(gradeService.getCourseAverage("C1")).thenReturn(8.5f);

        mockMvc.perform(get("/courses/C1/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("8.5"));

        verify(gradeService, times(1)).getCourseAverage("C1");
    }

    @Test
    void getCourseAverage_whenNoGrades_returns404() throws Exception
    {
        when(gradeService.getCourseAverage("C1")).thenThrow(new NoGradesRecordedException("Course", "C1"));

        mockMvc.perform(get("/courses/C1/average"))
                .andExpect(status().isNotFound());

        verify(gradeService, times(1)).getCourseAverage("C1");
    }
}
