package com.university.registry.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_courses")
@IdClass(StudentCourseId.class)
public class StudentCourses
{
    // Two @Id fields plus @IdClass means "the key is this pair"
    @Id
    private String studentAm;

    @Id
    private String courseId;

    protected StudentCourses() { }

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
