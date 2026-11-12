package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Grade;
import com.university.registry.model.StudentCourseId;
import com.university.registry.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business-logic layer for {@link Grade} objects.
 * <p>
 * Verifies that the student and course exist and that the student is enrolled before
 * recording a grade, rejects duplicate grades, and refuses to compute averages when
 * no grades exist. Delegates all persistence operations to {@link GradeRepository}.
 * <p>
 * All domain and validation exceptions thrown or propagated by this service are unchecked
 * and handled globally by the application's {@code @RestControllerAdvice} handler, while
 * database failures surface separately as Spring's unchecked {@code DataAccessException}.
 */
@Service
public class GradeService
{
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService; // Ensures grades are recorded only for enrolled students
    private final GradeRepository gradeRepository;

    /**
     * Constructs a {@code GradeService} with the required entity services and grade repository.
     *
     * @param studentService    the service used to validate {@link com.university.registry.model.Student} existence
     * @param courseService     the service used to validate {@link com.university.registry.model.Course} existence
     * @param enrollmentService the service used to verify student enrollment before grading
     * @param gradeRepository   the Spring Data JPA repository used for {@link Grade} persistence
     */
    public GradeService(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService, GradeRepository gradeRepository)
    {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.gradeRepository = gradeRepository;
    }

    /**
     * Records a grade for an enrolled student in a specific course.
     *
     * @param studentAm  the student's AM
     * @param courseId   the course's ID
     * @param gradeValue the numeric grade value to record
     * @throws IllegalArgumentException if {@code gradeValue} is outside the valid grade range
     * @throws EntityNotFoundException  if the student, the course, or the student's enrollment does not exist
     * @throws DuplicateEntityException if a grade is already recorded for this student and course
     */
    public void recordGrade(String studentAm, String courseId, float gradeValue)
    {
        Grade newGrade = new Grade(studentAm, courseId, gradeValue); // Construct first to reject an invalid grade without touching the database (fail fast)
        studentService.getStudentByAm(studentAm);
        courseService.getCourseById(courseId);

        if (!enrollmentService.isStudentEnrolled(studentAm, courseId))
        {
            throw new EntityNotFoundException("Εγγραφή", studentAm + "-" + courseId);
        }

        if (gradeRepository.existsById(new StudentCourseId(studentAm, courseId)))
        {
            throw new DuplicateEntityException("Βαθμός", studentAm + "-" + courseId);
        }

        gradeRepository.save(newGrade);
    }

    /**
     * Looks up the {@link Grade} of a specific student for a specific course.
     *
     * @param studentAm the student's AM (part of composite primary key)
     * @param courseId  the course's ID (part of composite primary key)
     * @return the matching {@link Grade}
     * @throws EntityNotFoundException if no grade is recorded for the given student and course
     */
    public Grade getGrade(String studentAm, String courseId)
    {
        Optional<Grade> grade = gradeRepository.findById(new StudentCourseId(studentAm, courseId));
        if (grade.isEmpty())
        {
            throw new EntityNotFoundException("Βαθμός", studentAm + "-" + courseId);
        }

        return grade.get();
    }

    /**
     * Retrieves all recorded grades for a specific student.
     *
     * @param studentAm the student's AM
     * @return a list containing all {@link Grade} records for the student
     * @throws EntityNotFoundException if no student exists with the given AM
     */
    public List<Grade> getGradesForStudent(String studentAm)
    {
        studentService.getStudentByAm(studentAm);
        return gradeRepository.findByStudentAm(studentAm);
    }

    /**
     * Calculates the grade average for a specific student.
     *
     * @param studentAm the student's AM
     * @return the float value of the student's grade average
     * @throws EntityNotFoundException   if no student exists with the given AM
     * @throws NoGradesRecordedException if the student has no recorded grades
     */
    public float getStudentAverage(String studentAm)
    {
        studentService.getStudentByAm(studentAm);

        if (!gradeRepository.existsByStudentAm(studentAm))
        {
            throw new NoGradesRecordedException("Φοιτητής", studentAm);
        }

        return gradeRepository.findAverageByStudentAm(studentAm).floatValue();
    }

    /**
     * Calculates the grade average for a specific course.
     *
     * @param courseId the course's ID
     * @return the float value of the course's grade average
     * @throws EntityNotFoundException   if no course exists with the given ID
     * @throws NoGradesRecordedException if the course has no recorded grades
     */
    public float getCourseAverage(String courseId)
    {
        courseService.getCourseById(courseId);

        if (!gradeRepository.existsByCourseId(courseId))
        {
            throw new NoGradesRecordedException("Μάθημα", courseId);
        }

        return gradeRepository.findAverageByCourseId(courseId).floatValue();
    }

    /**
     * Updates an existing {@link Grade} after verifying that it exists.
     *
     * @param grade the grade object containing the updated value
     * @throws EntityNotFoundException if no grade exists for the given student and course
     */
    public void updateGrade(Grade grade)
    {
        getGrade(grade.getStudentAm(), grade.getCourseId());
        gradeRepository.save(grade);
    }

    /**
     * Deletes a recorded {@link Grade} after verifying that it exists.
     *
     * @param studentAm the student's AM
     * @param courseId  the course's ID
     * @throws EntityNotFoundException if no grade exists for the given student and course
     */
    public void deleteGrade(String studentAm, String courseId)
    {
        getGrade(studentAm, courseId);
        gradeRepository.deleteById(new StudentCourseId(studentAm, courseId));
    }
}
