package com.university.registry.dev;

import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.Course;
import com.university.registry.model.Professor;
import com.university.registry.model.Student;
import com.university.registry.repository.CourseRepository;
import com.university.registry.repository.ProfessorRepository;
import com.university.registry.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class PersistenceSmokeTest implements CommandLineRunner
{
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;

    public PersistenceSmokeTest(StudentRepository studentRepository, ProfessorRepository professorRepository, CourseRepository courseRepository)
    {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception
    {
        testStudent();
        testProfessor();
        testCourse();
    }

    private void testStudent() throws Exception
    {
        Student student = new Student("AM9001", 1, "Spring test", "t@x.gr", "100");

        studentRepository.save(student);
        System.out.println("Stored: " + studentRepository.findById("AM9001").get());
        System.out.println("Students in the table: " + studentRepository.findAll().size());

        studentRepository.deleteById("AM9001");
        System.out.println("Still exists after delete? " + studentRepository.existsById("AM9001"));
        System.out.println();
    }

    private void testProfessor() throws Exception
    {
        Professor professor = new Professor("p9001", "Databases", "Dr Test", "p@x.gr", "200");

        professorRepository.save(professor);
        System.out.println("Stored: " + professorRepository.findById("P9001").get());
        System.out.println("Professors in the table: " + professorRepository.findAll().size());

        professorRepository.deleteById("P9001");
        System.out.println("Still exists after delete? " + professorRepository.existsById("P9001"));
        System.out.println();
    }

    private void testCourse() throws Exception
    {
        Course course = new Course("C9001", "Test Course", 3);

        courseRepository.save(course);
        System.out.println("Stored: " + courseRepository.findById("c9001").get());
        System.out.println("Courses in semester 3: " + courseRepository.findByCourseSemester(3));

        courseRepository.deleteById("C9001");
        System.out.println("Still exists after delete? " + courseRepository.existsById("c9001"));
        System.out.println();
    }
}
