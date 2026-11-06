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
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link ProfessorRepository}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
@Service
public class ProfessorService
{
    private final ProfessorRepository professorRepository;

    /**
     * @param professorRepository is the Interface Spring Data JPA uses to generate the implementation class.
     */
    public ProfessorService(ProfessorRepository professorRepository)
    {
        this.professorRepository = professorRepository;
    }

    /**
     * Adds a new Professor to the database.
     *
     * @param professor the professor to be added assumed already validated before being passed here
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (profId)
     */
    public void addProfessor(Professor professor) throws DuplicateEntityException
    {
        if (professorRepository.existsById(professor.getProfId()))
        {
            throw new DuplicateEntityException("Καθηγητής", professor.getProfId());
        }

        professorRepository.save(professor);
    }

    /**
     * Looks up for a specific Professor by its id.
     *
     * @param id the Professor's id (primary key)
     * @return the matching Professor
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public Professor getProfById(String id) throws EntityNotFoundException
    {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isEmpty())
        {
            throw new EntityNotFoundException("Καθηγητής", id);
        }

        return professor.get();
    }

    /**
     * Updates the Professor that's being passed as a parameter after first verifying its existence.
     *
     * @param updatedProfessor the Professor to be updated
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public void updateProfessor(Professor updatedProfessor) throws EntityNotFoundException
    {
        getProfById(updatedProfessor.getProfId());
        professorRepository.save(updatedProfessor);
    }

    /**
     * Deletes the Professor matching the given id after first verifying its existence.
     *
     * @param id the identifier of the Professor that's going to be deleted
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public void deleteProfessor(String id) throws EntityNotFoundException
    {
        getProfById(id);
        professorRepository.deleteById(id);
    }

    /**
     * Gets the list of all the Professor
     * objects stored in the database.
     *
     * @return a collection holding all the professors
     */
    public Collection<Professor> getAllProfessors()
    {
        return professorRepository.findAll();
    }
}
