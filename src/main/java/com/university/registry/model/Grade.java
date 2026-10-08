package com.university.registry.model;

import com.university.registry.exception.InvalidGradeException;

public class Grade
{
    private String studentAm;
    private String courseId;
    private float gradeValue;

    public Grade(String studentAm, String courseId, float gradeValue) throws InvalidGradeException
    {
        this.studentAm = studentAm.trim();
        this.courseId = courseId.trim();
        setGradeValue(gradeValue);
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

    public float getGradeValue()
    {
        return gradeValue;
    }

    public void setGradeValue(float gradeValue) throws InvalidGradeException
    {
        if (gradeValue < 0 || gradeValue > 10)
        {
            throw new InvalidGradeException(gradeValue);
        }
        this.gradeValue = gradeValue;
    }
}
