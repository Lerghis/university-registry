package com.university.registry.web.dto;

import com.university.registry.model.Student;

public record StudentResponse(String am, int semester, String name, String email, String phone)
{
    public static StudentResponse from(Student student)
    {
        // the entity-to-DTO mapping
        return new StudentResponse(student.getAm(), student.getSemester(),
                student.getName(), student.getEmail(), student.getPhone());
    }
}
