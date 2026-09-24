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
    private final Map<String, Student> studentsByAM;

    public StudentService()
    {
        this.studentsByAM = new HashMap<>(); // This is a manual constructor injection
    }

    public void addStudent(Student student) throws DuplicateEntityException
    {
        if (studentsByAM.containsKey(student.getAM()))
        {
            throw new DuplicateEntityException("Student", student.getAM());
        }

        studentsByAM.put(student.getAM(), student);
    }

    public Student getStudentByAM(String am) throws EntityNotFoundException
    {
        Student student = studentsByAM.get(am);
        if (student == null)
        {
            throw new EntityNotFoundException("Student", am);
        }

        return student;
    }

    public void updateStudent(Student updatedStudent) throws EntityNotFoundException
    {
        getStudentByAM(updatedStudent.getAM());
        studentsByAM.put(updatedStudent.getAM(), updatedStudent);
    }

    public void deleteStudent(String am) throws EntityNotFoundException
    {
        getStudentByAM(am);
        studentsByAM.remove(am);
    }

    public Collection<Student> getAllStudents()
    {
        return studentsByAM.values();
    }
}
