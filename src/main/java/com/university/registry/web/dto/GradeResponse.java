package com.university.registry.web.dto;

import com.university.registry.model.Grade;

public record GradeResponse(String studentAm, String courseId, Float value)
{
    public static GradeResponse from(Grade grade)
    {
        return new GradeResponse(grade.getStudentAm(), grade.getCourseId(), grade.getGradeValue());
    }
}
