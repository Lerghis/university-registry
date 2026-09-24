package com.university.registry.model;

import com.university.registry.exception.InvalidGradeException;

public class Grade
{
    private String studentAM;
    private String courseId;
    private float gradeValue;

    public Grade(String studentAM, String courseId, float gradeValue) throws InvalidGradeException
    {
        this.studentAM = studentAM.trim();
        this.courseId = courseId.trim();
        setGradeValue(gradeValue);
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
