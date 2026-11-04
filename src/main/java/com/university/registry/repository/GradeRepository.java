package com.university.registry.repository;

import com.university.registry.model.Grade;
import com.university.registry.model.StudentCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, StudentCourseId>
{
    List<Grade> findByStudentAm(String studentAm);
    boolean existsByStudentAm(String studentAm);
    boolean existsByCourseId(String courseId);

    @Query("select avg(g.gradeValue) from Grade g where g.studentAm = :studentAm")
    Double findAverageByStudentAm(@Param("studentAm") String studentAm);

    @Query("select avg(g.gradeValue) from Grade g where g.courseId = :courseId")
    Double findAverageByCourseId(@Param("courseId") String courseId);
}
