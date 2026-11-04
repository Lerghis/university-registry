package com.university.registry.repository;

import com.university.registry.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String>
{
}
