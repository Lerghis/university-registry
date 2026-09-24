package com.university.registry.service;


import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.InvalidGradeException;
import com.university.registry.exception.NoGradesRecordedException;
import com.university.registry.model.Course;
import com.university.registry.model.Grade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeService
{
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService; // to record grades only for student that are enrolled in courses
    private final Map<String, Grade> gradesByKey; // Encapsulation. The only way anything outside this class can affect grade data is through recordGrade()


    public GradeService(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService)
    {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.gradesByKey = new HashMap<>();
    }

    public void recordGrade(String studentAM, String courseId, float gradeValue) throws EntityNotFoundException, DuplicateEntityException, InvalidGradeException
    {
        /*
        Clean code - Fail Fast principle: student exists → course exists → enrollment exists → duplicate check → construct. This makes sure no precondition was violated before
        we reach the last operation.
         */
        studentService.getStudentByAM(studentAM); // This call is essentially an Abstraction. We do the call without knowing that the class is backed by a HashMap internally
        courseService.getCourseById(courseId); // Check if course exists

        List<Course> enrolledCourses = enrollmentService.getCoursesForStudent(studentAM);
        boolean isEnrolled = false;

        for (Course course : enrolledCourses) // check if enrollment exists
        {
            if (course.getCourseId().equals(courseId))
            {
                isEnrolled = true;
                break;
            }
        }

        if (!isEnrolled)
        {
            throw new EntityNotFoundException("Enrollment", studentAM + "-" + courseId);
        }

        String key = studentAM + "-" + courseId;

        if (gradesByKey.containsKey(key)) // duplicate check
        {
            throw new DuplicateEntityException("Grade", key);
        }

        Grade newGrade = new Grade(studentAM, courseId, gradeValue); // construct
        gradesByKey.put(key, newGrade); // add
    }

    public float getStudentAverage(String studentAM) throws EntityNotFoundException, NoGradesRecordedException
    {
        studentService.getStudentByAM(studentAM); // check if the student exists

        float sum = 0;
        int count = 0;

        for (Grade grade : gradesByKey.values())
        {
            if (grade.getStudentAM().equals(studentAM))
            {
                sum = sum + grade.getGradeValue();
                count++;
            }
        }

        if (count == 0)
        {
            throw new NoGradesRecordedException("Student", studentAM);
        }
        else
        {
            return sum / count;
        }
    }

    public float getCourseAverage(String courseId) throws EntityNotFoundException, NoGradesRecordedException
    {
        courseService.getCourseById(courseId); // check if the course exists
        return calculateAverage(courseId, false);
    }

    /**\
     * This method is private because it is an internal implementation detail. Nothing outside GradeService should ever call it directly with a raw boolean flag.
     * The two public methods (getStudentAverage/getCourseAverage) exist specifically to give callers a clear, self-explanatory entry point,
     * while the messy shared mechanics stay hidden behind them. This is the same encapsulation idea as your private HashMap fields — just applied to behavior instead of data.
     * @param identifier
     * @param matchByStudent
     * @return
     * @throws NoGradesRecordedException
     */
    private float calculateAverage(String identifier, boolean matchByStudent) throws NoGradesRecordedException
    {
        float sum = 0;
        int count = 0;

        for (Grade grade : gradesByKey.values())
        {
            String fieldToCompare = matchByStudent ? grade.getStudentAM() : grade.getCourseId();
            if (fieldToCompare.equals(identifier))
            {
                sum = sum + grade.getGradeValue();
                count++;
            }
        }

        if (count == 0)
        {
            throw new NoGradesRecordedException(matchByStudent ? "Student" : "Course", identifier);
        }
        else
        {
            return sum / count;
        }
    }
}
