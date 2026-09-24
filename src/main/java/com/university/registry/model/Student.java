package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;

public class Student extends Person
{
    private String AM;
    private int semester;


    public Student(String AM, int semester, String name, String email, String phone) throws InvalidSemesterException
    {
        super(name, email, phone);
        this.AM = AM.trim();
        setSemester(semester);
    }

    public String getAM()
    {
        return AM;
    }

    public void setAM(String AM)
    {
        this.AM = AM.trim();
    }

    public int getSemester()
    {
        return semester;
    }

    public void setSemester(int semester) throws InvalidSemesterException
    {
        if (semester < 1)
        {
            throw new InvalidSemesterException(semester);
        }
        this.semester = semester;
    }

    public String toString()
    {
        return "Αριθμός Μητρώου: " + AM + super.toString() + "\tΕξάμηνο: " + semester;
    }
}
