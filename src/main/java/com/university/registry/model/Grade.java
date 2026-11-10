package com.university.registry.model;

import com.university.registry.exception.InvalidGradeException;
import jakarta.persistence.*;

@Entity
@Table(name = "grades")
@IdClass(StudentCourseId.class)
public class Grade
{
    @Id
    private String studentAm;

    @Id
    private String courseId;

    /*
    The columnDefinition is here because the table column is DECIMAL(4,2)
    but the Java field is a float, and ddl-auto=validate compares those types.
    By declaring the column definition explicitly we make sure the Hibernate's validator won't complain
     */
    @Column(name = "grade_value", columnDefinition = "decimal(4,2)")
    private float gradeValue;

    protected Grade() { }

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
        this.studentAm = studentAm.trim();
    }

    public String getCourseId()
    {
        return courseId;
    }

    public void setCourseId(String courseId)
    {
        this.courseId = courseId.trim();
    }

    public float getGradeValue()
    {
        return gradeValue;
    }

    public void setGradeValue(float gradeValue) throws InvalidGradeException
    {
        if (!(gradeValue >= 0 && gradeValue <= 10)) // "not inside the range", so NaN is rejected too. It describes what is allowed, anything else is rejected.
        {
            throw new InvalidGradeException(gradeValue);
        }
        this.gradeValue = gradeValue;
    }

    @Override
    public String toString()
    {
        return "Βαθμός {" +
                "Αριθμός μητρώου φοιτητή='" + studentAm + '\'' +
                ", Κωδικός μαθήματος='" + courseId + '\'' +
                ", Τιμή βαθμού=" + gradeValue +
                '}';
    }
}
