package com.university.registry.model;

public class StudentCourses
{
    private String studentAm;
    private String courseId;

    public StudentCourses(String studentAm, String courseId)
    {
        this.studentAm = studentAm;
        this.courseId = courseId;
    }

    public String getStudentAm()
    {
        return studentAm;
    }

    public void setStudentAm(String studentAm)
    {
        this.studentAm = studentAm;
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
