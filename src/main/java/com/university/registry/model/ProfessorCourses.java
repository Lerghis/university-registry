package com.university.registry.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "professor_courses")
@IdClass(ProfessorCourseId.class)
public class ProfessorCourses
{
    @Id
    private String profId;

    @Id
    private String courseId;

    protected ProfessorCourses() { }

    public ProfessorCourses(String profId, String courseId)
    {
        this.profId = profId;
        this.courseId = courseId;
    }

    public String getProfId()
    {
        return profId;
    }

    public void setProfId(String profId)
    {
        this.profId = profId;
    }

    public String getCourseId()
    {
        return courseId;
    }

    public void setCourseId(String courseId)
    {
        this.courseId = courseId;
    }
}
