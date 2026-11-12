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
 * Enforces domain rules on stored data—throwing an {@link EntityNotFoundException}
 * when a requested record is missing or a {@link DuplicateEntityException} when
 * attempting to insert an existing record—and delegates persistence operations
 * to {@link StudentRepository}. Holds no state itself.
 * <p>
 * Domain exceptions are unchecked and handled globally by the application's
 * {@code @RestControllerAdvice} handler, while database failures propagate as
 * Spring's unchecked {@code DataAccessException}.
 */
@Service
public class StudentService
{
    private final StudentRepository studentRepository;

    /**
     * Constructs a new {@code StudentService} with the required repository dependency.
     * <p>
     * <b>Architecture Note (How Spring Data JPA Replaces the Old DAO):</b>
     * <ul>
     *   <li><b>What it replaced:</b> Previously, a manual {@code JdbcStudentDAO} class contained
     *       hand-written SQL queries and {@code ResultSet}-to-object mapping code.</li>
     *   <li><b>How Spring automates it:</b> At application startup, Spring Data JPA reads the
     *       {@link StudentRepository} interface and dynamically generates a proxy implementation
     *       class in memory (no manual implementation class is needed).</li>
     *   <li><b>ORM &amp; JPA under the hood:</b> Through the Jakarta Persistence API (JPA) and
     *       Hibernate as the ORM provider, Java objects ({@link Student} entities) are
     *       automatically mapped to database tables and translated into SQL queries.</li>
     *   <li><b>Runtime database driver:</b> Because the database driver is configured with
     *       {@code runtime} scope, our code depends only on standard JPA/Spring interfaces at
     *       compile time, while Hibernate uses the driver at runtime to communicate with the database.</li>
     * </ul>
     *
     * @param studentRepository the Spring Data JPA repository proxy injected at runtime for {@link Student} persistence
     */
    public StudentService(StudentRepository studentRepository)
    {
        this.studentRepository = studentRepository;
    }

    /**
     * Adds a new {@link Student} to the database after checking for duplicates.
     *
     * @param student the student to be added, assumed already validated before being passed here
     * @throws DuplicateEntityException if a student with the same AM already exists
     */
    public void addStudent(Student student)
    {
        if (studentRepository.existsById(student.getAm()))
        {
            throw new DuplicateEntityException("Φοιτητής", student.getAm());
        }

        studentRepository.save(student);
    }

    /**
     * Looks up a specific {@link Student} by their AM.
     *
     * @param am the student's AM (primary key)
     * @return the matching {@link Student}
     * @throws EntityNotFoundException if no student is found with the given AM
     */
    public Student getStudentByAm(String am)
    {
        Optional<Student> student = studentRepository.findById(am);
        if (student.isEmpty())
        {
            throw new EntityNotFoundException("Φοιτητής", am);
        }

        return student.get();
    }

    /**
     * Updates the given {@link Student} after verifying that it exists.
     *
     * @param updatedStudent the student containing updated data
     * @throws EntityNotFoundException if no student exists with the given AM
     */
    public void updateStudent(Student updatedStudent)
    {
        getStudentByAm(updatedStudent.getAm());
        studentRepository.save(updatedStudent);
    }

    /**
     * Deletes the {@link Student} matching the given AM after verifying that it exists.
     *
     * @param am the identifier of the student to be deleted
     * @throws EntityNotFoundException if no student exists with the given AM
     */
    public void deleteStudent(String am)
    {
        getStudentByAm(am);
        studentRepository.deleteById(am);
    }

    /**
     * Retrieves all {@link Student} objects stored in the database.
     *
     * @return a collection holding all stored students
     */
    public Collection<Student> getAllStudents()
    {
        return studentRepository.findAll();
    }
}
