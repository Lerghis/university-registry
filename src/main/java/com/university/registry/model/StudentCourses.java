package com.university.registry.model;

public class StudentCourses
{
    private String studentAM;
    private String courseId;

    public StudentCourses(String studentAM, String courseId)
    {
        this.studentAM = studentAM;
        this.courseId = courseId;
    }

    public String getStudentAM()
    {
        return studentAM;
    }

    public void setStudentAM(String studentAM)
    {
        this.studentAM = studentAM;
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
