package com.university.registry.model;

public class Professor extends Person
{
    private String profId;
    private String specialty;

    public Professor(String profId, String specialty, String name, String email, String phone)
    {
        super(name, email, phone);
        this.profId = profId.trim();
        this.specialty = specialty.trim();
    }

    public String getProfId()
    {
        return profId;
    }

    public void setProfId(String profId)
    {
        this.profId = profId.trim();
    }

    public String getSpecialty()
    {
        return specialty;
    }

    public void setSpecialty(String specialty)
    {
        this.specialty = specialty.trim();
    }

    @Override
    public String toString()
    {
        return "Καθηγητής {" +
                "κωδικός καθηγητή= '" + profId + '\'' +
                ", ειδικότητα= '" + specialty + '\'' +
                ", ονοματεπώνυμο= '" + name + '\'' +
                ", Email= '" + email + '\'' +
                ", τηλέφωνο= '" + phone + '\'' +
                '}';
    }
}
