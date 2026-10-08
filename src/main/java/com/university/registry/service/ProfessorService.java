package com.university.registry.service;

import com.university.registry.dao.ProfessorDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProfessorService
{
    private final ProfessorDAO professorDAO;

    public ProfessorService(ProfessorDAO professorDAO)
    {

        this.professorDAO = professorDAO;
    }

    public void addProfessor(Professor professor) throws DuplicateEntityException
    {
        if (professorDAO.findById(professor.getProfId()) != null)
        {
            throw new DuplicateEntityException("Professor", professor.getProfId());
        }

        professorDAO.insert(professor);
    }

    public Professor getProfById(String id) throws EntityNotFoundException
    {
        Professor professor = professorDAO.findById(id);
        if (professor == null)
        {
            throw new EntityNotFoundException("Professor", id);
        }

        return professor;
    }

    public void updateProfessor(Professor updatedProfessor) throws EntityNotFoundException
    {
        getProfById(updatedProfessor.getProfId());
        professorDAO.update(updatedProfessor);
    }

    public void deleteProfessor(String id) throws EntityNotFoundException
    {
        Professor professor = getProfById(id);
        professorDAO.delete(professor);
    }

    public Collection<Professor> getAllProfessors()
    {
        return professorDAO.findAll();
    }
}
