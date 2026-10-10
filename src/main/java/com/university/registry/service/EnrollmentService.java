package com.university.registry.service;

import com.university.registry.dao.EnrollmentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityInUseException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;

import java.util.List;

/**
 * Business-logic layer for relationships between entities: which students
 * are enrolled in which courses, and which professors teach them.
 * <p>
 * Holds the three entity services (composition) so it can confirm both
 * sides of a relationship exist before writing, and delegates all storage
 * to {@link EnrollmentDAO}. Also owns the "safe delete" operations: only
 * this class can see all three entity types without creating a circular
 * dependency.
 */
public class EnrollmentService
{
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService(StudentService studentService, ProfessorService professorService, CourseService courseService, EnrollmentDAO enrollmentDAO)
    {
        this.studentService = studentService;
        this.professorService = professorService;
        this.courseService = courseService;
        this.enrollmentDAO = enrollmentDAO;
    }

    /**
     * Enrolls a student in a course.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's id
     * @throws EntityNotFoundException  if the student or the course doesn't exist
     * @throws DuplicateEntityException if the student is already enrolled in it
     */
    public void assignCourseToStudent(String studentAm, String courseId) throws EntityNotFoundException, DuplicateEntityException
    {
        studentService.getStudentByAm(studentAm);
        courseService.getCourseById(courseId);

        if (enrollmentDAO.isStudentEnrolled(studentAm, courseId))
        {
            throw new DuplicateEntityException("Enrollment", studentAm + "-" + courseId);
        }

        enrollmentDAO.enrollStudent(studentAm, courseId);
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

        if (enrollmentDAO.isProfessorAssigned(profId, courseId))
        {
            throw new DuplicateEntityException("Assignment", profId + "-" + courseId);
        }

        enrollmentDAO.assignProfessor(profId, courseId);
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
        return enrollmentDAO.findCoursesForStudent(studentAm);
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
        return enrollmentDAO.findCoursesForProfessor(profId);
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

        if (enrollmentDAO.hasAnyEnrollmentForStudent(studentAm))
        {
            throw new EntityInUseException("Student", studentAm);
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

        if (enrollmentDAO.hasAnyAssignmentForProfessor(profId))
        {
            throw new EntityInUseException("Professor", profId);
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

        if (enrollmentDAO.hasAnyEnrollmentForCourse(courseId) || enrollmentDAO.hasAnyAssignmentForCourse(courseId))
        {
            throw new EntityInUseException("Course", courseId);
        }

        courseService.deleteCourse(courseId);
    }
}
