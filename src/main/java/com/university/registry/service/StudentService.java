package com.university.registry.service;

import com.university.registry.dao.StudentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;

import java.util.Collection;

public class StudentService
{
    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO)
    {
        this.studentDAO = studentDAO;
    }

    public void addStudent(Student student) throws DuplicateEntityException
    {
        if (studentDAO.findByAm(student.getAm()) != null)
        {
            throw new DuplicateEntityException("Student", student.getAm());
        }

        studentDAO.insert(student);
    }

    public Student getStudentByAm(String am) throws EntityNotFoundException
    {
        Student student = studentDAO.findByAm(am);
        if (student == null)
        {
            throw new EntityNotFoundException("Student", am);
        }

        return student;
    }

    public void updateStudent(Student updatedStudent) throws EntityNotFoundException
    {
        getStudentByAm(updatedStudent.getAm());
        studentDAO.update(updatedStudent);
    }

    public void deleteStudent(String am) throws EntityNotFoundException
    {
        Student student = getStudentByAm(am);
        studentDAO.delete(student);
    }

    public Collection<Student> getAllStudents()
    {
        return studentDAO.findAll();
    }
}
