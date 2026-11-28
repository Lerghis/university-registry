package com.university.registry.repository;

import com.university.registry.model.Course;
import com.university.registry.model.Grade;
import com.university.registry.model.Student;
import com.university.registry.model.StudentCourses;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentCoursesRepositoryTest
{
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentCoursesRepository studentCoursesRepository;

    @Test
    void findCoursesForStudent_returnsOnlyTheEnrolledCourses()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1));
        studentCoursesRepository.save(new StudentCourses("AM1", "C1"));
        studentCoursesRepository.saveAndFlush(new StudentCourses("AM2", "C2")); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        List<Course> courses = studentCoursesRepository.findCoursesForStudent("AM1");

        assertEquals(1, courses.size());
        assertEquals("C1", courses.getFirst().getCourseId());
    }

    @Test
    void deleteCourse_whenEnrolled_isRejectedByTheDatabase()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        courseRepository.save(new Course("C1", "Databases", 1));
        studentCoursesRepository.save(new StudentCourses("AM1", "C1"));

        courseRepository.deleteById("C1");

        try
        {
            courseRepository.flush();
            fail("Expected the foreign key to reject the delete");
        }
        catch (DataIntegrityViolationException ex)
        {
            // expected: ON DELETE RESTRICT did its job
        }
    }

    @Test
    void existsByStudentAm_whenExists_returnsTrue()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        courseRepository.save(new Course("C1", "Databases", 1));
        studentCoursesRepository.saveAndFlush(new StudentCourses("AM1", "C1")); // saveAndFlush so the INSERT runs in MariaDB

        boolean exists = studentCoursesRepository.existsByStudentAm("AM1");

        assertTrue(exists);
    }

    @Test
    void existsByStudentAm_whenDoesNotExist_returnsFalse()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        courseRepository.save(new Course("C1", "Databases", 1));
        studentRepository.save(new Student("AM2", 1, "Nikos", "n@x.gr", "200")); // decoy student
        courseRepository.save(new Course("C2", "Networks", 1)); // decoy course
        studentCoursesRepository.saveAndFlush(new StudentCourses("AM2", "C1"));

        boolean exists = studentCoursesRepository.existsByStudentAm("AM1");

        assertFalse(exists);
    }

    @Test
    void countGradesFor_whenGraded_returnsTheNumberOfGrades()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Nikos", "n@x.gr", "200")); // decoy student
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1)); // decoy course
        studentCoursesRepository.save(new StudentCourses("AM1", "C1"));
        studentCoursesRepository.save(new StudentCourses("AM1", "C2"));
        studentCoursesRepository.save(new StudentCourses("AM2", "C1"));

        gradeRepository.saveAndFlush(new Grade("AM1", "C1", 8.2f)); // saveAndFlush so the INSERT runs in MariaDB
        gradeRepository.save(new Grade("AM1", "C2", 9.0f)); // decoy: same student, wrong course
        gradeRepository.saveAndFlush(new Grade("AM2", "C1", 7.5f)); // decoy: wrong student, same course

        long gradeCount = studentCoursesRepository.countGradesFor("AM1", "C1");

        assertEquals(1, gradeCount);
    }

    @Test
    void countGradesFor_whenNoGrades_returnsZero()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Nikos", "n@x.gr", "200"));
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1));
        studentCoursesRepository.save(new StudentCourses("AM1", "C1")); // enrolled in C1, but no grade
        studentCoursesRepository.save(new StudentCourses("AM1", "C2"));
        studentCoursesRepository.save(new StudentCourses("AM2", "C1"));

        // Decoys: AM1 has a grade in C2, and AM2 has a grade in C1 — neither should count for (AM1, C1)
        gradeRepository.save(new Grade("AM1", "C2", 9.0f));
        gradeRepository.saveAndFlush(new Grade("AM2", "C1", 7.5f));

        long gradeCount = studentCoursesRepository.countGradesFor("AM1", "C1");

        assertEquals(0, gradeCount);
    }
}
