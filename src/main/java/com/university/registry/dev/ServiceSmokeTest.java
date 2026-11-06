package com.university.registry.dev;

import com.university.registry.model.Course;
import com.university.registry.model.Grade;
import com.university.registry.model.Student;
import com.university.registry.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ServiceSmokeTest implements CommandLineRunner
{
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;


    public ServiceSmokeTest(StudentService studentService,
                            ProfessorService professorService,
                            CourseService courseService,
                            EnrollmentService enrollmentService,
                            GradeService gradeService)
    {
        this.studentService = studentService;
        this.professorService = professorService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.gradeService = gradeService;
    }

    @Override
    public void run(String... args) throws Exception
    {
        testStudent();
    }

    private void testStudent() throws Exception
    {
        // add
        studentService.addStudent(new Student("AM9001", 2, "Spring Test", "t@x.gr", "100"));
        System.out.println("Stored: " + studentService.getStudentByAm("AM9001"));
        System.out.println("****************");

        // add
        courseService.addCourse(new Course("C9001", "Test Course", 2));
        System.out.println("Stored: " + courseService.getCourseById("C9001"));
        System.out.println("****************");

        // enroll
        enrollmentService.enrollStudentToCourse("AM9001", "C9001");
        System.out.println("Is that student enrolled? " + enrollmentService.isStudentEnrolled("AM9001", "C9001"));
        System.out.println("****************");

        // record grade
        gradeService.recordGrade("AM9001", "C9001", 8.5F);
        System.out.println("Grade for student AM9001: " + gradeService.getGrade("AM9001", "C9001"));
        System.out.println("****************");

        // get grades
        System.out.println("List of grades for student AM9001: " + gradeService.getGradesForStudent("AM9001"));

        // read the average
        System.out.println("Grade average for student AM9001: " + gradeService.getStudentAverage("AM9001"));
        System.out.println("****************");

        // update grade
        gradeService.updateGrade(new Grade("AM9001", "C9001", 5.5F));
        System.out.println("Updated grade is: " + gradeService.getGrade("AM9001", "C9001"));

        // EntityInUseException check (safely trying to delete course)
        enrollmentService.deleteCourseSafely("C9001");

        // EntityInUseException check (trying to unenroll a graded student)
        enrollmentService.unenrollStudent("AM9001", "C9001");
        System.out.println("****************");

        // missing student throws EntityNotFoundException
        enrollmentService.unenrollStudent("AM9001", "C9001");
    }
}
