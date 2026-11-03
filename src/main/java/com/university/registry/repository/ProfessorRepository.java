package com.university.registry.repository;

import com.university.registry.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, String>
{
}
