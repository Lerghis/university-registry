package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends Person
{
    @Id
    @Column(name = "am")
    private String am;
    private int semester;

    public Student(String am, int semester, String name, String email, String phone) throws InvalidSemesterException
    {
        super(name, email, phone);
        this.am = am.trim();
        setSemester(semester);
    }

    protected Student() { }

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
        return "Φοιτητής {" +
                "ΑΜ= '" + am + '\'' +
                ", εξάμηνο= " + semester +
                ", ονοματεπώνυμο= '" + name + '\'' +
                ", Email= '" + email + '\'' +
                ", τηλέφωνο= '" + phone + '\'' +
                '}';
    }
}
