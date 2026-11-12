package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;
import com.university.registry.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Business-logic layer for {@link Professor} objects.
 * <p>
 * Enforces domain rules on stored data—throwing an {@link EntityNotFoundException}
 * when a requested record is missing or a {@link DuplicateEntityException} when
 * attempting to insert an existing record—and delegates persistence operations
 * to {@link ProfessorRepository}. Holds no state itself.
 * <p>
 * Domain exceptions are unchecked and handled globally by the application's
 * {@code @RestControllerAdvice} handler, while database failures propagate as
 * Spring's unchecked {@code DataAccessException}.
 */
@Service
public class ProfessorService
{
    private final ProfessorRepository professorRepository;

    /**
     * Constructs a new {@code ProfessorService} with the required repository dependency.
     *
     * @param professorRepository the Spring Data JPA repository used for {@link Professor} persistence
     */
    public ProfessorService(ProfessorRepository professorRepository)
    {
        this.professorRepository = professorRepository;
    }

    /**
     * Adds a new {@link Professor} to the database.
     *
     * @param professor the professor to be added, assumed already validated before being passed here
     * @throws DuplicateEntityException if a professor with the same ID already exists
     */
    public void addProfessor(Professor professor)
    {
        if (professorRepository.existsById(professor.getProfId()))
        {
            throw new DuplicateEntityException("Καθηγητής", professor.getProfId());
        }

        professorRepository.save(professor);
    }

    /**
     * Looks up a specific {@link Professor} by their ID.
     *
     * @param id the professor's ID (primary key)
     * @return the matching {@link Professor}
     * @throws EntityNotFoundException if no professor is found with the given ID
     */
    public Professor getProfById(String id)
    {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isEmpty())
        {
            throw new EntityNotFoundException("Καθηγητής", id);
        }

        return professor.get();
    }

    /**
     * Updates the given {@link Professor} after verifying that it exists.
     *
     * @param updatedProfessor the professor containing updated data
     * @throws EntityNotFoundException if no professor exists with the given ID
     */
    public void updateProfessor(Professor updatedProfessor)
    {
        getProfById(updatedProfessor.getProfId());
        professorRepository.save(updatedProfessor);
    }

    /**
     * Deletes the {@link Professor} matching the given ID after verifying that it exists.
     *
     * @param id the identifier of the professor to be deleted
     * @throws EntityNotFoundException if no professor exists with the given ID
     */
    public void deleteProfessor(String id)
    {
        getProfById(id);
        professorRepository.deleteById(id);
    }

    /**
     * Retrieves all {@link Professor} objects stored in the database.
     *
     * @return a collection holding all stored professors
     */
    public Collection<Professor> getAllProfessors()
    {
        return professorRepository.findAll();
    }
}
