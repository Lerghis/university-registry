package com.university.registry.repository;

import com.university.registry.model.Course;
import com.university.registry.model.ProfessorCourseId;
import com.university.registry.model.ProfessorCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfessorCoursesRepository extends JpaRepository<ProfessorCourses, ProfessorCourseId>
{
    boolean existsByProfId(String profId);
    boolean existsByCourseId(String courseId);

    @Query("select c from Course c join ProfessorCourses pc on pc.courseId = c.courseId where pc.profId = :profId")
    List<Course> findCoursesForProfessor(@Param("profId") String profId);
}
