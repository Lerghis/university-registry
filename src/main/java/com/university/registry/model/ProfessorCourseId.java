package com.university.registry.model;

import java.io.Serializable;
import java.util.Objects;

public class ProfessorCourseId implements Serializable
{
    private String profId;
    private String courseId;

    public ProfessorCourseId() { }

    public ProfessorCourseId(String profId, String courseId)
    {
        this.profId = profId;
        this.courseId = courseId;
    }

    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProfessorCourseId that = (ProfessorCourseId) object;
        return Objects.equals(profId, that.profId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(profId, courseId);
    }
}
