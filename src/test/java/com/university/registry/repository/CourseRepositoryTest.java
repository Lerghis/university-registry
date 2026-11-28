package com.university.registry.repository;

import com.university.registry.model.Course;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseRepositoryTest
{
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindById_roundTrip_preservesAllFields()
    {
        courseRepository.saveAndFlush(new Course("C1", "Databases", 1));

        entityManager.flush();
        entityManager.clear();

        Optional<Course> found = courseRepository.findById("C1");

        assertTrue(found.isPresent());
        assertEquals("C1", found.get().getCourseId());
        assertEquals("Databases", found.get().getCourseTitle());
        assertEquals(1, found.get().getCourseSemester());
    }
}
