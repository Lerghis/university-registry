package com.university.registry.repository;

import com.university.registry.model.Course;
import com.university.registry.model.Professor;
import com.university.registry.model.ProfessorCourses;
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
public class ProfessorCoursesRepositoryTest
{
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProfessorCoursesRepository professorCoursesRepository;

    @Test
    void findCoursesForProfessor_returnsOnlyTheAssignedCourses()
    {
        professorRepository.save(new Professor("P1", "Data Analyst", "Professor 1", "p1@x.gr", "001"));
        professorRepository.save(new Professor("P2", "Programmer", "Professor 2", "p2@x.gr", "002")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C3", "C Programming", 2));
        professorCoursesRepository.save(new ProfessorCourses("P1", "C1"));
        professorCoursesRepository.saveAndFlush(new ProfessorCourses("P2", "C3")); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        List<Course> courses = professorCoursesRepository.findCoursesForProfessor("P1");

        assertEquals(1, courses.size());
        assertEquals("C1", courses.getFirst().getCourseId());
    }

    @Test
    void existsByProfId_whenExists_returnsTrue()
    {
        professorRepository.save(new Professor("P1", "Data Analyst", "Professor 1", "p@x.gr", "001"));
        professorRepository.save(new Professor("P2", "Programmer", "Professor 2", "p2@x.gr", "002")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C3", "C Programming", 2)); // a decoy that query must ignore
        professorCoursesRepository.save(new ProfessorCourses("P1", "C1"));
        professorCoursesRepository.saveAndFlush(new ProfessorCourses("P2", "C3")); // a decoy that query must ignore & saveAndFlush so the INSERT runs in MariaDB

        boolean exists = professorCoursesRepository.existsByProfId("P1");

        assertTrue(exists);
    }

    @Test
    void existsByProfId_whenDoesNotExist_returnsFalse()
    {
        // the record below is a decoy
        professorRepository.save(new Professor("P1", "Data Analyst", "Professor 1", "p@x.gr", "001"));
        professorRepository.save(new Professor("P2", "Programmer", "Professor 2", "p2@x.gr", "002"));
        courseRepository.save(new Course("C1", "Databases", 1));
        professorCoursesRepository.saveAndFlush(new ProfessorCourses("P2", "C1")); // saveAndFlush so the INSERT runs in MariaDB

        boolean exists = professorCoursesRepository.existsByProfId("P1");

        assertFalse(exists);
    }

    @Test
    void existsByCourseId_whenExists_returnsTrue()
    {
        professorRepository.save(new Professor("P1", "Data Analyst", "Professor 1", "p@x.gr", "001"));
        professorRepository.save(new Professor("P2", "Programmer", "Professor 2", "p2@x.gr", "002")); // a decoy that query must ignore
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C3", "C Programming", 2)); // a decoy that query must ignore
        professorCoursesRepository.save(new ProfessorCourses("P1", "C1"));
        professorCoursesRepository.saveAndFlush(new ProfessorCourses("P2", "C3")); // a decoy that query must ignore  saveAndFlush so the INSERT runs in MariaDB

        boolean exists = professorCoursesRepository.existsByCourseId("C1");

        assertTrue(exists);
    }

    @Test
    void existsByCourseId_whenDoesNotExist_returnsFalse()
    {
        // the record below is a decoy
        professorRepository.save(new Professor("P1", "Data Analyst", "Professor 1", "p@x.gr", "001"));
        courseRepository.save(new Course("C1", "Databases", 1));
        courseRepository.save(new Course("C3", "C Programming", 2));
        professorCoursesRepository.saveAndFlush(new ProfessorCourses("P1", "C3")); // saveAndFlush so the INSERT runs in MariaDB

        boolean exists = professorCoursesRepository.existsByCourseId("C1");

        assertFalse(exists);
    }
}
