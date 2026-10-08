package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;

public class Student extends Person
{
    private String am;
    private int semester;


    public Student(String am, int semester, String name, String email, String phone) throws InvalidSemesterException
    {
        super(name, email, phone);
        this.am = am.trim();
        setSemester(semester);
    }

    public String getAm()
    {
        return am;
    }

    public void setAm(String am)
    {
        this.am = am.trim();
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

    @Override
    public String toString()
    {
        return "Student{" +
                "am='" + am + '\'' +
                ", semester=" + semester +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
