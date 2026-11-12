package com.university.registry.web.dto;

import com.university.registry.model.Professor;

public record ProfessorResponse(String profId, String specialty, String name, String email, String phone)
{
    public static ProfessorResponse from(Professor professor)
    {
        return new ProfessorResponse(professor.getProfId(), professor.getSpecialty(),
                professor.getName(), professor.getEmail(), professor.getPhone());
    }
}
