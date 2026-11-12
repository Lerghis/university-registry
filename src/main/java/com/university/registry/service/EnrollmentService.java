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
 * Business-logic layer managing relationships between entities: which students
 * are enrolled in which courses, and which professors teach them.
 * <p>
 * Composes {@link StudentService}, {@link ProfessorService}, and {@link CourseService}
 * to verify that both sides of a relationship exist before writing, and delegates
 * relationship persistence to {@link StudentCoursesRepository} and
 * {@link ProfessorCoursesRepository}. It also centralizes "safe delete" operations,
 * preventing deletion of entities that still have active enrollments or assignments
 * without introducing circular dependencies among the core entity services.
 * <p>
 * All domain exceptions thrown or propagated by this service are unchecked and
 * handled globally by the application's {@code @RestControllerAdvice} handler, while
 * database failures surface as Spring's unchecked {@code DataAccessException}.
 */
@Service
public class EnrollmentService
{
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final StudentCoursesRepository studentCoursesRepository;
    private final ProfessorCoursesRepository professorCoursesRepository;

    /**
     * Constructs an {@code EnrollmentService} with all required entity services and relationship repositories.
     *
     * @param studentService             the service used to validate and manage {@link Student} entities
     * @param professorService           the service used to validate and manage {@link Professor} entities
     * @param courseService              the service used to validate and manage {@link Course} entities
     * @param studentCoursesRepository   the repository managing student-course enrollments
     * @param professorCoursesRepository the repository managing professor-course assignments
     */
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
     * Enrolls a student in a course after verifying that both exist and are not already linked.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's ID
     * @throws EntityNotFoundException  if either the student or the course does not exist
     * @throws DuplicateEntityException if the student is already enrolled in the course
     */
    public void enrollStudentToCourse(String studentAm, String courseId)
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
     * Unenrolls a student from a course after verifying the enrollment exists and has no recorded grades.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's ID
     * @throws EntityNotFoundException if the student, the course, or the enrollment record does not exist
     * @throws EntityInUseException    if the student already has recorded grades for this course
     */
    public void unenrollStudent(String studentAm, String courseId)
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
     * Assigns a professor to teach a course after verifying that both exist and are not already linked.
     *
     * @param profId   the professor's ID
     * @param courseId the course's ID
     * @throws EntityNotFoundException  if either the professor or the course does not exist
     * @throws DuplicateEntityException if the professor is already assigned to the course
     */
    public void assignCourseToProfessor(String profId, String courseId)
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
     * Unassigns a professor from a course after verifying that the assignment exists.
     *
     * @param profId   the professor's ID
     * @param courseId the course's ID
     * @throws EntityNotFoundException if the professor, the course, or the assignment record does not exist
     */
    public void unassignProfessor(String profId, String courseId)
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
     * Retrieves all courses a student is currently enrolled in.
     *
     * @param studentAm the student's AM
     * @return a list of {@link Course} objects the student is enrolled in
     * @throws EntityNotFoundException if no student exists with the given AM
     */
    public List<Course> getCoursesForStudent(String studentAm)
    {
        studentService.getStudentByAm(studentAm);
        return studentCoursesRepository.findCoursesForStudent(studentAm);
    }

    /**
     * Retrieves all courses assigned to a specific professor.
     *
     * @param profId the professor's ID
     * @return a list of {@link Course} objects assigned to the professor
     * @throws EntityNotFoundException if no professor exists with the given ID
     */
    public List<Course> getCoursesForProfessor(String profId)
    {
        professorService.getProfById(profId);
        return professorCoursesRepository.findCoursesForProfessor(profId);
    }

    /**
     * Deletes a student only if they have no active course enrollments.
     *
     * @param studentAm the AM of the student to delete
     * @throws EntityNotFoundException if no student exists with the given AM
     * @throws EntityInUseException    if the student is still enrolled in one or more courses
     */
    public void deleteStudentSafely(String studentAm)
    {
        studentService.getStudentByAm(studentAm);

        if (studentCoursesRepository.existsByStudentAm(studentAm))
        {
            throw new EntityInUseException("Φοιτητής", studentAm);
        }

        studentService.deleteStudent(studentAm);
    }

    /**
     * Deletes a professor only if they have no active course assignments.
     *
     * @param profId the ID of the professor to delete
     * @throws EntityNotFoundException if no professor exists with the given ID
     * @throws EntityInUseException    if the professor is still assigned to one or more courses
     */
    public void deleteProfessorSafely(String profId)
    {
        professorService.getProfById(profId);

        if (professorCoursesRepository.existsByProfId(profId))
        {
            throw new EntityInUseException("Καθηγητής", profId);
        }

        professorService.deleteProfessor(profId);
    }

    /**
     * Deletes a course only if no students are enrolled in it and no professors are assigned to it.
     *
     * @param courseId the ID of the course to delete
     * @throws EntityNotFoundException if no course exists with the given ID
     * @throws EntityInUseException    if the course still has enrolled students or assigned professors
     */
    public void deleteCourseSafely(String courseId)
    {
        courseService.getCourseById(courseId);

        if (studentCoursesRepository.existsByCourseId(courseId) || professorCoursesRepository.existsByCourseId(courseId))
        {
            throw new EntityInUseException("Μάθημα", courseId);
        }

        courseService.deleteCourse(courseId);
    }

    /**
     * Checks whether a student is enrolled in a specific course.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's ID
     * @return {@code true} if the student is enrolled in the course, or {@code false} otherwise
     */
    public boolean isStudentEnrolled(String studentAm, String courseId)
    {
        return studentCoursesRepository.existsById(new StudentCourseId(studentAm, courseId));
    }
}
