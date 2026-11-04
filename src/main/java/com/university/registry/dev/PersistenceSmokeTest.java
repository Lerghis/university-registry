package com.university.registry.dev;

import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.*;
import com.university.registry.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class PersistenceSmokeTest implements CommandLineRunner
{
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final StudentCoursesRepository studentCoursesRepository;
    private final ProfessorCoursesRepository professorCoursesRepository;
    private final GradeRepository gradeRepository;

    public PersistenceSmokeTest(StudentRepository studentRepository, ProfessorRepository professorRepository, CourseRepository courseRepository, StudentCoursesRepository studentCoursesRepository, ProfessorCoursesRepository professorCoursesRepository, GradeRepository gradeRepository)
    {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.studentCoursesRepository = studentCoursesRepository;
        this.professorCoursesRepository = professorCoursesRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public void run(String... args) throws Exception
    {
        testStudent();
        testProfessor();
        testCourse();
        testStudentCourses();
        testProfessorCourses();
        testGrades();
    }

    private void testStudent() throws Exception
    {
        Student student = new Student("AM9001", 1, "Spring test", "t@x.gr", "100");

        studentRepository.save(student);
        System.out.println("Stored: " + studentRepository.findById("AM9001").get());
        System.out.println("Students in the table: " + studentRepository.findAll().size());

        //studentRepository.deleteById("AM9001");
        System.out.println("Still exists after delete? " + studentRepository.existsById("AM9001"));
        System.out.println();
    }

    private void testProfessor() throws Exception
    {
        Professor professor = new Professor("p9001", "Databases", "Dr Test", "p@x.gr", "200");

        professorRepository.save(professor);
        System.out.println("Stored: " + professorRepository.findById("P9001").get());
        System.out.println("Professors in the table: " + professorRepository.findAll().size());

        //professorRepository.deleteById("P9001");
        System.out.println("Still exists after delete? " + professorRepository.existsById("P9001"));
        System.out.println();
    }

    private void testCourse() throws Exception
    {
        Course course = new Course("C9001", "Test Course", 3);

        courseRepository.save(course);
        System.out.println("Stored: " + courseRepository.findById("c9001").get());

        //courseRepository.deleteById("C9001");
        System.out.println("Still exists after delete? " + courseRepository.existsById("c9001"));
        System.out.println();
    }

    private void testStudentCourses() throws Exception
    {
        StudentCourses studentCourse = new StudentCourses("AM9001", "C9001");
        studentCoursesRepository.save(studentCourse);
        System.out.println("Stored: " + studentCoursesRepository.existsById(new StudentCourseId("AM9001", "C9001")));
        System.out.println(studentCoursesRepository.findCoursesForStudent("AM9001").size());
        System.out.println();
    }

    private void testProfessorCourses() throws Exception
    {
        ProfessorCourses professorCourse = new ProfessorCourses("P9001", "C9001");
        professorCoursesRepository.save(professorCourse);
        System.out.println("Stored: " + professorCoursesRepository.existsById(new ProfessorCourseId("P9001", "C9001")));
        System.out.println();
        System.out.println(professorCoursesRepository.findCoursesForProfessor("P9001").size());
    }

    private void testGrades() throws Exception
    {
        Grade grade = new Grade("AM9001", "C9001", 8.5F);
        gradeRepository.save(grade);
        System.out.println("Stored: " + gradeRepository.existsById(new StudentCourseId("AM9001", "C9001")));
        System.out.println("AVG: " + gradeRepository.findAverageByStudentAm("AM9001"));
        System.out.println("Count grades for student: " + studentCoursesRepository.countGradesFor("AM9001", "C9001"));
        System.out.println();

//        System.out.println("Try deleting the course while the enrollment still exists to see the ON DELETE RESTRICT database defense taking place:");
//        courseRepository.deleteById("C9001");
    }
}
