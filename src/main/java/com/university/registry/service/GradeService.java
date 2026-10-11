package com.university.registry.service;

import com.university.registry.dao.GradeDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidGradeException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Grade;

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
            throw new EntityNotFoundException("Enrollment", studentAm + "-" + courseId);
        }

        if (gradeDAO.exists(studentAm, courseId))
        {
            throw new DuplicateEntityException("Grade", studentAm + "-" + courseId);
        }

        gradeDAO.insert(newGrade);
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
            throw new NoGradesRecordedException("Student", studentAm);
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
            throw new NoGradesRecordedException("Course", courseId);
        }

        return gradeDAO.findCourseAverage(courseId);
    }

    /**
     * Updates the value of a grade.
     *
     * @param grade the grade to be updated
     * @throws EntityNotFoundException if the student or the course or the grade doesn't exist
     */
    public void updateGrade(Grade grade) throws EntityNotFoundException
    {
        studentService.getStudentByAm(grade.getStudentAm());
        courseService.getCourseById(grade.getCourseId());

        if (!gradeDAO.exists(grade.getStudentAm(), grade.getCourseId()))
        {
            throw new EntityNotFoundException("Grade", grade.getStudentAm() + "-" + grade.getCourseId());
        }

        gradeDAO.update(grade);
    }

    /**
     * Deletes a grade.
     *
     * @param grade the grade to be deleted
     * @throws EntityNotFoundException if the student or the course or the grade doesn't exist
     */
    public void deleteGrade(Grade grade) throws EntityNotFoundException
    {
        studentService.getStudentByAm(grade.getStudentAm());
        courseService.getCourseById(grade.getCourseId());

        if (!gradeDAO.exists(grade.getStudentAm(), grade.getCourseId()))
        {
            throw new EntityNotFoundException("Grade", grade.getStudentAm() + "-" + grade.getCourseId());
        }

        gradeDAO.delete(grade);
    }
}
