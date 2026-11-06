package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Student;
import com.university.registry.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Business-logic layer for {@link Student} objects.
 * <p>
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link StudentRepository}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
@Service
public class StudentService
{
    private final StudentRepository studentRepository;
    /**
     *
     * @param studentRepository is the Interface Spring Data JPA uses to generate the implementation class (JdbcStudentDAO),
     *                          which was the class with the SQL queries that communicated with the database.
     *                          Now, with the runtime scope dependency, the driver is needed when the program runs
     *                          and that's how everything talks to the database now (interfaces only), without the need
     *                          of manually writing the SQL and the mapping code.
     *                          <p></p>
     *                          Java automates this with ORM (object-relational-mapping) utilizing the JPA (Jakarta Persistence)
     *                          API, implemented its database actions by a tool like Hibernate.
     */
    public StudentService(StudentRepository studentRepository)
    {
        this.studentRepository = studentRepository;
    }

    /**
     * Adds a new Student to the database after a duplication check.
     *
     * @param student the student to be added assumed already validated before being passed here
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (am)
     */
    public void addStudent(Student student) throws DuplicateEntityException
    {
        if (studentRepository.existsById(student.getAm()))
        {
            throw new DuplicateEntityException("Φοιτητής", student.getAm());
        }

        studentRepository.save(student);
    }

    /**
     * Looks up for a specific Student by its am.
     *
     * @param am the Student's am (primary key)
     * @return the matching Student
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public Student getStudentByAm(String am) throws EntityNotFoundException
    {
        Optional<Student> student = studentRepository.findById(am);
        if (student.isEmpty())
        {
            throw new EntityNotFoundException("Φοιτητής", am);
        }

        return student.get();
    }

    /**
     * Updates the Student that's being passed as a parameter after first verifying its existence.
     *
     * @param updatedStudent the Student to be updated
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public void updateStudent(Student updatedStudent) throws EntityNotFoundException
    {
        getStudentByAm(updatedStudent.getAm());
        studentRepository.save(updatedStudent);
    }

    /**
     * Deletes the Student matching the given am after first verifying its existence.
     *
     * @param am the identifier of the Student that's going to be deleted
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public void deleteStudent(String am) throws EntityNotFoundException
    {
        getStudentByAm(am);
        studentRepository.deleteById(am);
    }

    /**
     * Gets the list of all the Student
     * objects stored in the database.
     *
     * @return a collection holding all the students
     */
    public Collection<Student> getAllStudents()
    {
        return studentRepository.findAll();
    }
}
