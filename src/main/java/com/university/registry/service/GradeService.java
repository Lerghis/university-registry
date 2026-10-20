package com.university.registry.service;

import com.university.registry.dao.GradeDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidGradeException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Grade;

import java.util.List;

/**
 /**
 * Business-logic layer for grades. Verifies that the student and course
 * exist and that the student is enrolled before recording a grade,
 * rejects duplicates, and refuses to compute an average when no grades exist.
 * Delegates all storage to {@link GradeDAO}.
 * <p>
 * All these exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */

public class GradeService
{
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService; // to record grades only for student that are enrolled in courses
    private final GradeDAO gradeDAO;

    public GradeService(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService, GradeDAO gradeDAO)
    {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.gradeDAO = gradeDAO;
    }

    /**
     * Records a grade in an enrolled student.
     *
     * @param studentAm the student's AM
     * @param courseId the course's id
     * @param gradeValue the value of the grade
     * @throws EntityNotFoundException if the student or the course or the enrollment doesn't exist
     * @throws DuplicateEntityException if a grade has already been recorded
     * @throws InvalidGradeException if the grade to be recorded has an invalid value
     */
    public void recordGrade(String studentAm, String courseId, float gradeValue) throws EntityNotFoundException, DuplicateEntityException, InvalidGradeException
    {
        Grade newGrade = new Grade(studentAm, courseId, gradeValue); // Construct first to reject an invalid grade without touching the database (fail fast)
        studentService.getStudentByAm(studentAm);
        courseService.getCourseById(courseId);

        if (!enrollmentService.isStudentEnrolled(studentAm, courseId))
        {
            throw new EntityNotFoundException("Εγγραφή", studentAm + "-" + courseId);
        }

        if (gradeDAO.findByStudentAndCourse(studentAm, courseId) != null)
        {
            throw new DuplicateEntityException("Βαθμός", studentAm + "-" + courseId);
        }

        gradeDAO.insert(newGrade);
    }

    /**
     * Looks for the Grade of a specific Student for a specific Course by calling {@link GradeDAO}'s {@code findByStudentAndCourse()} method.
     *
     * @param studentAm the Student's am (primary key)
     * @param courseId the Course's id (primary key)
     * @return the matching Grade
     * @throws EntityNotFoundException if there is no recorded grade in the database
     */
    public Grade getGrade(String studentAm, String courseId) throws EntityNotFoundException
    {
        Grade grade = gradeDAO.findByStudentAndCourse(studentAm, courseId);
        if (grade == null)
        {
            throw new EntityNotFoundException("Βαθμός", studentAm + "-" + courseId);
        }

        return grade;
    }

    /**
     * Calls {@link GradeDAO}'s {@code findByStudent()} method to get the list of all the grades of that Student
     *
     * @param studentAm the Student's am (primary key)
     * @return the list with all the grades
     * @throws EntityNotFoundException if the Student isn't found in the database
     */
    public List<Grade> getGradesForStudent(String studentAm) throws EntityNotFoundException
    {
        studentService.getStudentByAm(studentAm);
        return gradeDAO.findByStudent(studentAm);
    }

    /**
     * Gets the grade average of a student.
     *
     * @param studentAm the student's AM
     * @return the float value of the student's grade average
     * @throws EntityNotFoundException if the student doesn't exist
     * @throws NoGradesRecordedException if the student has no grades recorded
     */
    public float getStudentAverage(String studentAm) throws EntityNotFoundException, NoGradesRecordedException
    {
        studentService.getStudentByAm(studentAm);

        if (!gradeDAO.hasAnyGradeForStudent(studentAm))
        {
            throw new NoGradesRecordedException("Φοιτητής", studentAm);
        }

        return gradeDAO.findStudentAverage(studentAm);
    }

    /**
     * Gets the grade average of a course.
     *
     * @param courseId the course's id
     * @return the float value of the course's grade average
     * @throws EntityNotFoundException if the course doesn't exist
     * @throws NoGradesRecordedException if the course has no grades recorded
     */
    public float getCourseAverage(String courseId) throws EntityNotFoundException, NoGradesRecordedException
    {
        courseService.getCourseById(courseId);

        if (!gradeDAO.hasAnyGradeForCourse(courseId))
        {
            throw new NoGradesRecordedException("Μάθημα", courseId);
        }

        return gradeDAO.findCourseAverage(courseId);
    }

    /**
     * Updates the value of a grade.
     *
     * @param grade the grade to be updated
     * @throws EntityNotFoundException if the grade doesn't exist
     */
    public void updateGrade(Grade grade) throws EntityNotFoundException
    {
        getGrade(grade.getStudentAm(), grade.getCourseId());
        gradeDAO.update(grade);
    }

    /**
     * Deletes a grade.
     *
     * @param studentAm the Student's am
     * @param courseId the Course's id
     * @throws EntityNotFoundException if the grade doesn't exist
     */
    public void deleteGrade(String studentAm, String courseId) throws EntityNotFoundException
    {
        getGrade(studentAm, courseId);
        gradeDAO.delete(studentAm, courseId);
    }
}
