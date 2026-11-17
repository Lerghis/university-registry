package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;
import com.university.registry.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfessorServiceTest
{
    private ProfessorRepository professorRepository;
    private ProfessorService professorService;

    @BeforeEach
    void setup()
    {
        professorRepository = mock(ProfessorRepository.class);
        professorService = new ProfessorService(professorRepository);
    }

    @Test
    void addProfessor_withNewProfId_savesProfessor()
    {
        Professor newProfessor = new Professor("P1", "Programmer", "Georgiou", "g@x.gr", "100");
        when(professorRepository.existsById("P1")).thenReturn(false);

        professorService.addProfessor(newProfessor);

        verify(professorRepository, times(1)).save(newProfessor);
    }

    @Test
    void addProfessor_withExistingId_throwsDuplicateEntityException()
    {
        when(professorRepository.existsById("P1")).thenReturn(true); // program the mock

        try
        {
            professorService.addProfessor(new Professor("P1", "Programmer", "Nikolaou", "n@x.gr", "100"));
            fail("Expected a DuplicateEntityException but nothing was thrown");
        }
        catch (DuplicateEntityException ex)
        {
            verify(professorRepository, never()).save(any()); // nothing may be saved
        }
    }

    @Test
    void getProfessorById_whenMissing_throwsEntityNotFoundException()
    {
        when(professorRepository.findById("P1")).thenReturn(Optional.empty());

        try
        {
            professorService.getProfById("P1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            // expected: the test passes
        }
    }

    @Test
    void updateProfessor_whenMissing_throwsEntityNotFoundException()
    {
        when(professorRepository.findById("P1")).thenReturn(Optional.empty());

        try
        {
            professorService.updateProfessor(new Professor("P1", "Programmer", "Georgiou", "g@x.gr", "100"));
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorRepository, never()).save(any());
        }
    }

    @Test
    void updateProfessor_whenExists_changesTheStoredValues()
    {
        Professor existingProfessor = new Professor("P1", "Programmer", "Georgiou", "g@x.gr", "100");
        Professor newInfo = new Professor("P1", "Programmer", "Nikolaou", "n@x.gr", "100");

        // Tells the mock repo that "AM1" exists so getStudentByAm() doesn't throw an exception
        when(professorRepository.findById("P1")).thenReturn(Optional.of(existingProfessor));

        professorService.updateProfessor(newInfo);

        // VERIFY: Prove that save() was called specifically with 'newInfo'
        verify(professorRepository, times(1)).save(newInfo);
    }

    @Test
    void deleteProfessor_whenMissing_throwsEntityNotFoundException()
    {
        when(professorRepository.findById("P1")).thenReturn(Optional.empty());

        try
        {
            professorService.deleteProfessor("P1");
            fail("Expected an EntityNotFoundException but nothing was thrown");
        }
        catch (EntityNotFoundException ex)
        {
            verify(professorRepository, never()).deleteById(any());
        }
    }

    @Test
    void deleteProfessor_whenExists_removesIt()
    {
        Professor existingProfessor = new Professor("P1", "Programmer", "Georgiou", "g@x.gr", "100");
        when(professorRepository.findById("P1")).thenReturn(Optional.of(existingProfessor));

        professorService.deleteProfessor("P1");

        verify(professorRepository, times(1)).deleteById("P1");
    }

    @Test
    void getAllProfessors_whenEmpty_returnsAnEmptyCollection()
    {
        when(professorRepository.findAll()).thenReturn(List.of());

        boolean isEmpty = professorService.getAllProfessors().isEmpty();

        assertTrue(isEmpty);

        verify(professorRepository, times(1)).findAll();
    }
}
