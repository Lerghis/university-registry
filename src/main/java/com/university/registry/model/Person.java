package com.university.registry.model;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // “Person is not a table. Its fields (name, email, phone) become columns in the table of every subclass.”
public class Person
{
    protected String name;
    protected String email;
    protected String phone;

    protected Person() { }

    public Person(String name, String email, String phone)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
