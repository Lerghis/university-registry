package com.university.registry.service;

import com.university.registry.dao.ProfessorDAO;
import com.university.registry.dao.StudentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;
import com.university.registry.model.Student;

import java.util.Collection;

/**
 * Business-logic layer for {@link Professor} objects.
 * <p>
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link ProfessorDAO}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
public class ProfessorService
{
    private final ProfessorDAO professorDAO;

    /**
     *
     * @param professorDAO is the Data Access Object for the {@code professors} table
     *                   which is being injected via this constructor, because
     *                   {@code ProfessorService} needs access to its methods
     */
    public ProfessorService(ProfessorDAO professorDAO)
    {

        this.professorDAO = professorDAO;
    }

    /**
     * Adds a new Professor to the database by calling {@link ProfessorDAO}'s {@code insert()} method.
     *
     * @param professor the professor to be added assumed already validated before being passed here.
     *
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (profId).
     */
    public void addProfessor(Professor professor) throws DuplicateEntityException
    {
        if (professorDAO.findById(professor.getProfId()) != null)
        {
            throw new DuplicateEntityException("Professor", professor.getProfId());
        }

        professorDAO.insert(professor);
    }

    /**
     * Looks up for a specific Professor by its id by calling {@link ProfessorDAO}'s {@code findById()} method
     *
     * @param id the Professor's id (primary key)
     *
     * @return the matching {@link Professor}
     *
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public Professor getProfById(String id) throws EntityNotFoundException
    {
        Professor professor = professorDAO.findById(id);
        if (professor == null)
        {
            throw new EntityNotFoundException("Professor", id);
        }

        return professor;
    }

    /**
     * Updates the {@link Professor} that's being passed as a parameter by calling {@link ProfessorDAO}'s
     * {@code update()} method, after first verifying its existence with {@code getProfById()}
     *
     * @param updatedProfessor the {@link Professor} to be updated
     *
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public void updateProfessor(Professor updatedProfessor) throws EntityNotFoundException
    {
        getProfById(updatedProfessor.getProfId());
        professorDAO.update(updatedProfessor);
    }

    /**
     * Deletes the {@link Professor} matching the given id by calling {@link ProfessorDAO}'s
     * {@code delete()} method, after first verifying its existence with {@code getProfById()}
     *
     * @param id the identifier of the Professor that's going to be deleted
     *
     * @throws EntityNotFoundException if the Professor isn't found in the database
     */
    public void deleteProfessor(String id) throws EntityNotFoundException
    {
        Professor professor = getProfById(id);
        professorDAO.delete(professor);
    }

    /**
     * Calls {@link ProfessorDAO}'s {@code findAll()} method to get the list of all the Professor
     * objects stored in the database
     *
     * @return a collection holding all the professors
     */
    public Collection<Professor> getAllProfessors()
    {
        return professorDAO.findAll();
    }
}
