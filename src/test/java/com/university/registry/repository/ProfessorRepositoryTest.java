package com.university.registry.repository;

import com.university.registry.model.Professor;
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
public class ProfessorRepositoryTest
{
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindById_roundTrip_preservesAllFields()
    {
        professorRepository.saveAndFlush(new Professor("P1", "Data Analyst", "Professor 1", "p@x.gr", "001"));

        entityManager.flush();
        entityManager.clear();

        Optional<Professor> found = professorRepository.findById("P1");

        assertTrue(found.isPresent());
        assertEquals("P1", found.get().getProfId());
        assertEquals("Data Analyst", found.get().getSpecialty());
        assertEquals("Professor 1", found.get().getName());
        assertEquals("p@x.gr", found.get().getEmail());
        assertEquals("001", found.get().getPhone());
    }
}
