package com.university.registry.dao;

import com.university.registry.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fake StudentDAO for tests: it keeps the students in a HashMap, so no database is needed.
 */
public class InMemoryStudentDAO implements StudentDAO
{
    private final Map<String, Student> studentsByAm = new HashMap<>();

    @Override
    public void insert(Student student)
    {
        studentsByAm.put(student.getAm(), student);
    }

    @Override
    public Student findByAm(String am)
    {
        return studentsByAm.get(am);
    }

    @Override
    public List<Student> findAll()
    {
        return new ArrayList<>(studentsByAm.values());
    }

    @Override
    public void update(Student student)
    {
        studentsByAm.put(student.getAm(), student);
    }

    @Override
    public void deleteByAm(String studentAm)
    {
        studentsByAm.remove(studentAm);
    }
}
