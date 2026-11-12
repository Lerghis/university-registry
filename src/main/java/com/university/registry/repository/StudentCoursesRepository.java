package com.university.registry.repository;

import com.university.registry.model.Course;
import com.university.registry.model.StudentCourseId;
import com.university.registry.model.StudentCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentCoursesRepository extends JpaRepository<StudentCourses, StudentCourseId>
{
    boolean existsByStudentAm(String studentAm);
    boolean existsByCourseId(String courseId);

    @Query("select c from Course c join StudentCourses sc on sc.courseId = c.courseId where sc.studentAm = :studentAm")
    List<Course> findCoursesForStudent(@Param("studentAm") String studentAm);

    @Query("select count(g) from Grade g where g.studentAm = :studentAm and g.courseId = :courseId")
    long countGradesFor(@Param("studentAm") String studentAm, @Param("courseId") String courseId);
}
