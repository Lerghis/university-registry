package com.university.registry.repository;

import com.university.registry.model.Student;
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
public class StudentRepositoryTest
{
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindById_roundTrip_preservesAllFields()
    {
        // 1. ARRANGE: Create and save a Student
        studentRepository.saveAndFlush(new Student("AM1", 1, "Maria", "m@x.gr", "100"));

        entityManager.flush();
        entityManager.clear();

        // 2. ACT: Read the Student back by its primary key (AM)
        Optional<Student> found = studentRepository.findById("AM1");

        // 3. ASSERT: Verify the record was found and every column mapped accurately
        assertTrue(found.isPresent());
        assertEquals("AM1", found.get().getAm());
        assertEquals(1, found.get().getSemester());
        assertEquals("Maria", found.get().getName());
        assertEquals("m@x.gr", found.get().getEmail());
        assertEquals("100", found.get().getPhone());
    }
}
