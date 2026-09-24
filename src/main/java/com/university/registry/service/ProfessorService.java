package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Professor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProfessorService
{
    private final Map<String, Professor> professorsById;

    public ProfessorService()
    {
        this.professorsById = new HashMap<>();
    }

    public void addProfessor(Professor professor) throws DuplicateEntityException
    {
        if (professorsById.containsKey(professor.getProfId()))
        {
            throw new DuplicateEntityException("Professor", professor.getProfId());
        }

        professorsById.put(professor.getProfId(), professor);
    }

    public Professor getProfById(String id) throws EntityNotFoundException
    {
        Professor professor = professorsById.get(id);
        if (professor == null)
        {
            throw new EntityNotFoundException("Professor", id);
        }

        return professor;
    }

    public void updateProfessor(Professor updatedProfessor) throws EntityNotFoundException
    {
        getProfById(updatedProfessor.getProfId());
        professorsById.put(updatedProfessor.getProfId(), updatedProfessor);
    }

    public void deleteProfessor(String id) throws EntityNotFoundException
    {
        getProfById(id);
        professorsById.remove(id);
    }

    public Collection<Professor> getAllProfessors()
    {
        return professorsById.values();
    }
}
