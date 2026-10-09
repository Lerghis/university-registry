package com.university.registry.service;

import com.university.registry.dao.StudentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;

import java.util.Collection;

/**
 * Business-logic layer for {@link Student} objects.
 * <p>
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link StudentDAO}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
public class StudentService
{
    private final StudentDAO studentDAO;

    /**
     *
     * @param studentDAO is the Data Access Object for the {@code students} table
     *                   which is being injected via this constructor, because
     *                   {@code StudentService} needs access to its methods
     */
    public StudentService(StudentDAO studentDAO)
    {
        this.studentDAO = studentDAO;
    }

    /**
     * Adds a new Student to the database by calling {@link StudentDAO}'s {@code insert()} method.
     *
     * @param student the student to be added assumed already validated before being passed here.
     *
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (am).
     */
    public void addStudent(Student student) throws DuplicateEntityException
    {
        if (studentDAO.findByAm(student.getAm()) != null)
        {
            throw new DuplicateEntityException("Student", student.getAm());
        }

        studentDAO.insert(student);
    }

    /**
     * Looks up for a specific Student by its am by calling {@link StudentDAO}'s {@code findByAm()} method
     *
     * @param am the Student's am (primary key)
     *
     * @return the matching {@link Student}
     *
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public Student getStudentByAm(String am) throws EntityNotFoundException
    {
        Student student = studentDAO.findByAm(am);
        if (student == null)
        {
            throw new EntityNotFoundException("Student", am);
        }

        return student;
    }

    /**
     * Updates the {@link Student} that's being passed as a parameter by calling {@link StudentDAO}'s
     * {@code update()} method, after first verifying its existence with {@code getStudentByAm()}
     *
     * @param updatedStudent the {@link Student} to be updated
     *
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public void updateStudent(Student updatedStudent) throws EntityNotFoundException
    {
        getStudentByAm(updatedStudent.getAm());
        studentDAO.update(updatedStudent);
    }

    /**
     * Deletes the {@link Student} matching the given am by calling {@link StudentDAO}'s
     * {@code delete()} method, after first verifying its existence with {@code getStudentByAm()}
     *
     * @param am the identifier of the Student that's going to be deleted
     *
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public void deleteStudent(String am) throws EntityNotFoundException
    {
        Student student = getStudentByAm(am);
        studentDAO.delete(student);
    }

    /**
     * Calls {@link StudentDAO}'s {@code findAll()} method to get the list of all the Student
     * objects stored in the database
     *
     * @return a collection holding all the students
     */
    public Collection<Student> getAllStudents()
    {
        return studentDAO.findAll();
    }
}
