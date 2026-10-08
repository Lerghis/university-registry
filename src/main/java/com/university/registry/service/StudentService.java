package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StudentService
{
    // Keyed by AM for O(1) lookup
    private final Map<String, Student> studentsByAm;

    public StudentService()
    {
        this.studentsByAm = new HashMap<>();
    }

    public void addStudent(Student student) throws DuplicateEntityException
    {
        if (studentsByAm.containsKey(student.getAm()))
        {
            throw new DuplicateEntityException("Student", student.getAm());
        }

        studentsByAm.put(student.getAm(), student);
    }

    public Student getStudentByAm(String am) throws EntityNotFoundException
    {
        Student student = studentsByAm.get(am);
        if (student == null)
        {
            throw new EntityNotFoundException("Student", am);
        }

        return student;
    }

    public void updateStudent(Student updatedStudent) throws EntityNotFoundException
    {
        getStudentByAm(updatedStudent.getAm());
        studentsByAm.put(updatedStudent.getAm(), updatedStudent);
    }

    public void deleteStudent(String am) throws EntityNotFoundException
    {
        getStudentByAm(am);
        studentsByAm.remove(am);
    }

    public Collection<Student> getAllStudents()
    {
        return studentsByAm.values();
    }
}
