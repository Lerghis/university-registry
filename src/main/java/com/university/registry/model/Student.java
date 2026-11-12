package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // “This class is stored in a table.”
@Table(name = "students") // By default, the table would be called student, so we need to specify what the real name is
public class Student extends Person
{
    @Id // “This field is the primary key.”
    @Column(name = "am") // “The column is called am.”
    private String am;

    // (nothing) The column name defaults to the field name in snake_case, so semester becomes semester and name becomes name.
    private int semester;

    protected Student() { }

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
        return "Φοιτητής {" +
                "ΑΜ= '" + am + '\'' +
                ", εξάμηνο= " + semester +
                ", ονοματεπώνυμο= '" + name + '\'' +
                ", Email= '" + email + '\'' +
                ", τηλέφωνο= '" + phone + '\'' +
                '}';
    }
}
