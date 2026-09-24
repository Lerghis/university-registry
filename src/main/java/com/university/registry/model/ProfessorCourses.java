package com.university.registry.model;

public class ProfessorCourses
{
    private String profId;
    private String courseId;

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
