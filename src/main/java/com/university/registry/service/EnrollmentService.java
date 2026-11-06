package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.*;
import com.university.registry.repository.ProfessorCoursesRepository;
import com.university.registry.repository.StudentCoursesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business-logic layer for relationships between entities: which students
 * are enrolled in which courses, and which professors teach them.
 * <p>
 * Holds the three entity services (composition) so it can confirm both
 * sides of a relationship exist before writing, and delegates all storage
 * to {@link StudentCoursesRepository} and {@link ProfessorCoursesRepository}. Also owns the "safe delete" operations: only
 * this class can see all three entity types without creating a circular dependency.
 */
@Service
public class EnrollmentService
{
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final StudentCoursesRepository studentCoursesRepository;
    private final ProfessorCoursesRepository professorCoursesRepository;

    public EnrollmentService(StudentService studentService, ProfessorService professorService, CourseService courseService,
                             StudentCoursesRepository studentCoursesRepository, ProfessorCoursesRepository professorCoursesRepository)
    {
        this.studentService = studentService;
        this.professorService = professorService;
        this.courseService = courseService;
        this.studentCoursesRepository = studentCoursesRepository;
        this.professorCoursesRepository = professorCoursesRepository;
    }

    /**
     * Enrolls a student in a course.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's id
     * @throws EntityNotFoundException  if the student or the course doesn't exist
     * @throws DuplicateEntityException if the student is already enrolled in it
     */
    public void enrollStudentToCourse(String studentAm, String courseId) throws EntityNotFoundException, DuplicateEntityException
    {
        studentService.getStudentByAm(studentAm);
        courseService.getCourseById(courseId);

        if (studentCoursesRepository.existsById(new StudentCourseId(studentAm, courseId)))
        {
            throw new DuplicateEntityException("Εγγραφή", studentAm + "-" + courseId);
        }

        studentCoursesRepository.save(new StudentCourses(studentAm, courseId));
    }

    /**
     * Unenrolls a student from a course.
     *
     * @param studentAm the student's AM
     * @param courseId the course's id
     * @throws EntityNotFoundException if the student or the course doesn't exist
     * @throws EntityInUseException if the student is graded for that enrollment
     */
    public void unenrollStudent(String studentAm, String courseId) throws EntityNotFoundException, EntityInUseException
    {
        studentService.getStudentByAm(studentAm);
        courseService.getCourseById(courseId);

        if (!studentCoursesRepository.existsById(new StudentCourseId(studentAm, courseId)))
        {
            throw new EntityNotFoundException("Εγγραφή", studentAm + "-" + courseId);
        }

        if (studentCoursesRepository.countGradesFor(studentAm, courseId) > 0)
        {
            throw new EntityInUseException("Εγγραφή", studentAm + "-" + courseId);
        }

        studentCoursesRepository.deleteById(new StudentCourseId(studentAm, courseId));
    }

    /**
     * Assigns a professor in a course.
     *
     * @param profId the professor's id
     * @param courseId  the course's id
     * @throws EntityNotFoundException  if the professor or the course doesn't exist
     * @throws DuplicateEntityException if the professor is already assigned in it
     */
    public void assignCourseToProfessor(String profId, String courseId) throws EntityNotFoundException, DuplicateEntityException
    {
        professorService.getProfById(profId);
        courseService.getCourseById(courseId);

        if (professorCoursesRepository.existsById(new ProfessorCourseId(profId, courseId)))
        {
            throw new DuplicateEntityException("Ανάθεση", profId + "-" + courseId);
        }

        professorCoursesRepository.save(new ProfessorCourses(profId, courseId));
    }

    /**
     * Unassigns a professor from a course.
     *
     * @param profId the professor's id
     * @param courseId  the course's id
     * @throws EntityNotFoundException  if the professor or the course or the assignment doesn't exist
     */
    public void unassignProfessor(String profId, String courseId) throws EntityNotFoundException
    {
        professorService.getProfById(profId);
        courseService.getCourseById(courseId);

        if (!professorCoursesRepository.existsById(new ProfessorCourseId(profId, courseId)))
        {
            throw new EntityNotFoundException("Ανάθεση", profId + "-" + courseId);
        }

        professorCoursesRepository.deleteById(new ProfessorCourseId(profId, courseId));
    }

    /**
     * Gets all the courses a student is enrolled to.
     *
     * @param studentAm the student's AM
     * @return the list with courses
     * @throws EntityNotFoundException if the student doesn't exist
     */
    public List<Course> getCoursesForStudent(String studentAm) throws EntityNotFoundException
    {
        studentService.getStudentByAm(studentAm);
        return studentCoursesRepository.findCoursesForStudent(studentAm);
    }

    /**
     * Gets all the courses a professor is assigned to.
     *
     * @param profId the professor's id
     * @return the list with courses
     * @throws EntityNotFoundException if the professor doesn't exist
     */
    public List<Course> getCoursesForProfessor(String profId) throws EntityNotFoundException
    {
        professorService.getProfById(profId);
        return professorCoursesRepository.findCoursesForProfessor(profId);
    }

    /**
     * Deletes a student only if no enrollment references them.
     *
     * @param studentAm the AM of the student to delete
     * @throws EntityNotFoundException if the student doesn't exist
     * @throws EntityInUseException    if the student is enrolled in any course
     */
    public void deleteStudentSafely(String studentAm) throws EntityNotFoundException, EntityInUseException
    {
        studentService.getStudentByAm(studentAm);

        if (studentCoursesRepository.existsByStudentAm(studentAm))
        {
            throw new EntityInUseException("Φοιτητής", studentAm);
        }

        studentService.deleteStudent(studentAm);
    }

    /**
     * Deletes a student only if no assignment references them.
     *
     * @param profId the id of the professor to delete
     * @throws EntityNotFoundException if the professor doesn't exist
     * @throws EntityInUseException    if the professor is assigned in any course
     */
    public void deleteProfessorSafely(String profId) throws EntityNotFoundException, EntityInUseException
    {
        professorService.getProfById(profId);

        if (professorCoursesRepository.existsByProfId(profId))
        {
            throw new EntityInUseException("Καθηγητής", profId);
        }

        professorService.deleteProfessor(profId);
    }

    /**
     * Deletes a course only if no assignment references them.
     *
     * @param courseId the id of the course to delete
     * @throws EntityNotFoundException if the course doesn't exist
     * @throws EntityInUseException    if there are any enrollments or assignments referencing this course
     */
    public void deleteCourseSafely(String courseId) throws EntityNotFoundException, EntityInUseException
    {
        courseService.getCourseById(courseId);

        if (studentCoursesRepository.existsByCourseId(courseId) || professorCoursesRepository.existsByCourseId(courseId))
        {
            throw new EntityInUseException("Μάθημα", courseId);
        }

        courseService.deleteCourse(courseId);
    }

    /**
     * Checks if a Student is enrolled in a specific Course.
     *
     * @param studentAm the Student's am
     * @param courseId the Course's id
     * @return true if the Student is enrolled, false if it isn't
     */
    public boolean isStudentEnrolled(String studentAm, String courseId)
    {
        return studentCoursesRepository.existsById(new StudentCourseId(studentAm, courseId));
    }
}
