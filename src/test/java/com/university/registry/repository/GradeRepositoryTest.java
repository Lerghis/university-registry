package com.university.registry.repository;

import com.university.registry.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GradeRepositoryTest
{
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCoursesRepository studentCoursesRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAverageByStudentAm_whenGradesExist_returnsAverageValue()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1));
        saveEnrollment("AM1", "C1");
        saveEnrollment("AM1", "C2");
        saveEnrollment("AM2", "C2");
        gradeRepository.save(new Grade("AM1", "C1", 8));
        gradeRepository.save(new Grade("AM1", "C2", 9));
        gradeRepository.saveAndFlush(new Grade("AM2", "C2", 3)); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        Double average = gradeRepository.findAverageByStudentAm("AM1");

        assertNotNull(average);
        assertEquals(8.5, average, 0.001);
    }

    @Test
    void findAverageByStudentAm_whenNoGrades_returnsNull()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1));
        saveEnrollment("AM1", "C1");
        saveEnrollment("AM2", "C1");
        saveEnrollment("AM2", "C2");
        gradeRepository.save(new Grade("AM2", "C1", 9));
        gradeRepository.saveAndFlush(new Grade("AM2", "C2", 3)); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        Double average = gradeRepository.findAverageByStudentAm("AM1");

        assertNull(average);
    }

    @Test
    void findAverageByCourseId_whenGradesExist_returnsAverageValue()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200"));
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1)); // a decoy that query must ignore
        saveEnrollment("AM1", "C1");
        saveEnrollment("AM2", "C1");
        saveEnrollment("AM2", "C2");
        gradeRepository.save(new Grade("AM1", "C1", 8));
        gradeRepository.save(new Grade("AM2", "C1", 9));
        gradeRepository.saveAndFlush(new Grade("AM2", "C2", 3)); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        Double average = gradeRepository.findAverageByCourseId("C1");

        assertNotNull(average);
        assertEquals(8.5, average, 0.001);
    }

    @Test
    void findAverageByCourseId_whenNoGrades_returnsNull()
    {
        Double average = gradeRepository.findAverageByCourseId("C1");

        assertNull(average);
    }

    @Test
    void existsByStudentAm_withNoGrades_returnsFalse()
    {
        // the record below is a decoy
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200"));
        courseRepository.save(new Course("C1", "Databases", 1));
        saveEnrollment("AM1", "C1");
        saveEnrollment("AM2", "C1");
        gradeRepository.saveAndFlush(new Grade("AM2", "C1", 9)); // saveAndFlush so the INSERT runs in MariaDB

        boolean exists = gradeRepository.existsByStudentAm("AM1");

        assertFalse(exists);
    }

    @Test
    void existsByCourseId_withNoGrades_returnsFalse()
    {
        // the record below is a decoy
        studentRepository.save(new Student("AM2", 1, "Eleni", "e@x.gr", "200"));
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1));
        saveEnrollment("AM2", "C1");
        saveEnrollment("AM2", "C2");
        gradeRepository.saveAndFlush(new Grade("AM2", "C2", 9)); // saveAndFlush so the INSERT runs in MariaDB

        boolean exists = gradeRepository.existsByCourseId("C1");

        assertFalse(exists);
    }

    @Test
    void findByStudentAm_whenGradesExist_returnsGrades()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C2", "Networks", 1)); // a decoy that query must ignore
        saveEnrollment("AM1", "C1");
        saveEnrollment("AM1", "C2");
        gradeRepository.save(new Grade("AM1", "C1", 8));
        gradeRepository.save(new Grade("AM1", "C2", 9));

        entityManager.flush(); // 1. Forces SQL INSERT to execute in MariaDB right now
        entityManager.clear(); // 2. Clears Hibernate's memory so the next line MUST read from MariaDB

        List<Grade> found = gradeRepository.findByStudentAm("AM1");

        assertTrue(gradeRepository.existsById(new StudentCourseId("AM1", "C1")));
        assertTrue(gradeRepository.existsById(new StudentCourseId("AM1", "C2")));
        assertEquals(2, found.size());
        assertEquals(8, found.get(0).getGradeValue());
        assertEquals(9, found.get(1).getGradeValue());
    }

    @Test
    void savedGrade_keepsItsExactValueWhenReadBack()
    {
        studentRepository.save(new Student("AM1", 1, "Maria", "m@x.gr", "100"));
        courseRepository.save(new Course("C1", "Databases", 1));
        saveEnrollment("AM1", "C1");
        gradeRepository.save(new Grade("AM1", "C1", 8.1f));

        entityManager.flush(); // 1. Forces SQL INSERT to execute in MariaDB right now
        entityManager.clear(); // 2. Clears Hibernate's memory so the next line MUST read from MariaDB

        List<Grade> found = gradeRepository.findByStudentAm("AM1");

        assertTrue(gradeRepository.existsById(new StudentCourseId("AM1", "C1")));
        assertEquals(8.1f, found.getFirst().getGradeValue(), 0.001f);
    }

    private void saveEnrollment(String am, String courseId)
    {
        studentCoursesRepository.saveAndFlush(new StudentCourses(am, courseId));
    }
}
