package com.university.registry.controller;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.ProfessorService;
import com.university.registry.web.ProfessorController;
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

@WebMvcTest(ProfessorController.class)
public class ProfessorControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessorService professorService;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void listProfessors_whenHasItems_returns200() throws Exception
    {
        List<Professor> professors = List.of(new Professor("P1", "Programmer", "Professor 1", "p1@x.gr", "001"),
                                             new Professor("P2", "AI Engineer", "Professor 2", "p2@x.gr", "002"));

        when(professorService.getAllProfessors()).thenReturn(professors);

        mockMvc.perform(get("/professors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listProfessors_whenEmpty_returnAnEmptyList() throws Exception
    {
        when(professorService.getAllProfessors()).thenReturn(List.of());

        mockMvc.perform(get("/professors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProfessor_whenExists_return200AndJson() throws Exception
    {
        when(professorService.getProfById("P1")).thenReturn(new Professor("P1", "Programmer", "Professor 1", "p1@x.gr", "001"));

        mockMvc.perform(get("/professors/P1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profId").value("P1"))
                .andExpect(jsonPath("$.name").value("Professor 1"));
    }

    @Test
    void getProfessor_whenMissing_returns404() throws Exception
    {
        when(professorService.getProfById("P1")).thenThrow(new EntityNotFoundException("Professor", "P1"));

        mockMvc.perform(get("/professors/P1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createProfessor_whenCreated_returns201() throws Exception
    {
        mockMvc.perform(post("/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"profId":"P1","specialty":"Programmer","name":"Professor 1","email":"p1@x.gr","phone":"001"}
                        """))
                .andExpect(status().isCreated());

        verify(professorService, times(1)).addProfessor(any());
    }

    @Test
    void createProfessor_whenDuplicate_returns409() throws Exception
    {
        doThrow(new DuplicateEntityException("Professor", "P1")).when(professorService).addProfessor(any());

        mockMvc.perform(post("/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"profId":"P1","specialty":"Programmer","name":"Professor 1","email":"p1@x.gr","phone":"001"}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void createProfessor_withEmptyBody_returns400() throws Exception
    {
        mockMvc.perform(post("/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(professorService, never()).addProfessor(any());
    }

    @Test
    void updateProfessor_whenUpdated_returns200() throws Exception
    {
        mockMvc.perform(put("/professors/P1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"specialty":"Data Analyst","name":"Professor 1","email":"p1@x.gr","phone":"001"}
                        """))
                .andExpect((status().isOk()));

        verify(professorService, times(1)).updateProfessor(any());
    }

    @Test
    void updateProfessor_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Professor", "P1")).when(professorService).updateProfessor(any());

        mockMvc.perform(put("/professors/P1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"specialty":"Data Analyst","name":"Professor 1","email":"p1@x.gr","phone":"001"}
                        """))
                .andExpect((status().isNotFound()))
                .andExpect(jsonPath("$.status").value(404));

        verify(professorService, times(1)).updateProfessor(any());
    }

    @Test
    void updateProfessor_withEmptyBody_returns400AndDoesNotCallTheService() throws Exception
    {
        mockMvc.perform(put("/professors/P1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(professorService, never()).updateProfessor(any());
    }

    @Test
    void deleteProfessor_whenDeleted_returns204() throws Exception
    {
        mockMvc.perform(delete("/professors/P1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).deleteProfessorSafely("P1");
    }

    @Test
    void deleteProfessor_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Professor", "P1")).when(enrollmentService).deleteProfessorSafely("P1");

        mockMvc.perform(delete("/professors/P1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).deleteProfessorSafely("P1");
    }
}
