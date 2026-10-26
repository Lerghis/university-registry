package com.university.registry.dao;

import com.university.registry.model.Student;

import java.util.List;

public interface StudentDAO
{
    void insert(Student student);

    Student findByAm(String am);

    List<Student> findAll();

    void update(Student student);

    void deleteByAm(String studentAm);
}
