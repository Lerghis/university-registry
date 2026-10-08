package com.university.registry.dao;

import com.university.registry.exception.DataAccessException;
import com.university.registry.model.Grade;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for the {@code grades} table.
 * <p>
 * Replaces the in-memory {@code gradesByKey} map and the manual sum/count
 * loop that used to live in {@code GradeService}. Contains no business
 * rules: whether a student is enrolled, or whether "no grades yet" should
 * be an error, are decisions that belong to {@code GradeService}.
 */
public class GradeDAO
{
    private final DataSourceFactory dataSourceFactory;

    /**
     * @param dataSourceFactory the shared connection pool factory
     */
    public GradeDAO(DataSourceFactory dataSourceFactory)
    {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Inserts a new grade row.
     *
     * @param grade the grade to be recorded; assumed already validated by
     *              {@link Grade}'s own constructor
     * @throws DataAccessException if the insert fails, including a duplicate
     *                              (student_am, course_id) primary key
     */
    public void insert(Grade grade)
    {
        String sql = "INSERT INTO grades (student_am, course_id, grade_value) VALUES (?, ?, ?)";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, grade.getStudentAm());
            stmt.setString(2, grade.getCourseId());
            stmt.setFloat(3, grade.getGradeValue());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to insert grade", ex);
        }
    }

    /**
     * Replaces the {@code containsKey} duplicate check in
     * {@code GradeService.recordGrade}.
     *
     * @return {@code true} if a grade already exists for this student and course
     */
    public boolean exists(String studentAM, String courseId)
    {
        String sql = "SELECT COUNT(*) FROM grades WHERE student_am = ? AND course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, studentAM);
            stmt.setString(2, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if the grade exists", ex);
        }
    }

    /**
     * Lets the Service decide whether to throw {@code NoGradesRecordedException}
     * before asking for an average.
     *
     * @return {@code true} if the student has at least one recorded grade
     */
    public boolean hasAnyGradeForStudent(String studentAm)
    {
        String sql = "SELECT COUNT(*) FROM grades WHERE student_am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, studentAm);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify grades for student", ex);
        }
    }

    /**
     * @return {@code true} if the course has at least one recorded grade
     */
    public boolean hasAnyGradeForCourse(String courseId)
    {
        String sql = "SELECT COUNT(*) FROM grades WHERE course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify grades for course", ex);
        }
    }

    /**
     * Computes the student's average directly in SQL.
     * <p>
     * <b>Callers must first confirm grades exist</b> via
     * {@link #hasAnyGradeForStudent}. With zero rows, SQL's {@code AVG}
     * yields NULL, which JDBC's {@code getFloat} silently turns into
     * {@code 0.0} - indistinguishable from a real average of zero.
     */
    public float findStudentAverage(String studentAm)
    {
        String sql = "SELECT AVG(grade_value) FROM grades WHERE student_am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, studentAm);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getFloat(1);
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to calculate grade average for student", ex);
        }
    }

    /**
     * Computes the course's average directly in SQL.
     * Same precondition as {@link #findStudentAverage}: check
     * {@link #hasAnyGradeForCourse} first.
     */
    public float findCourseAverage(String courseId)
    {
        String sql = "SELECT AVG(grade_value) FROM grades WHERE course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                return rs.getFloat(1);
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to calculate grade average for course", ex);
        }
    }

    /**
     * Changes the value of an existing grade, matched by (student_am, course_id).
     */
    public void update(Grade grade)
    {
        String sql = "UPDATE grades SET grade_value = ? WHERE student_am = ? AND course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setFloat(1, grade.getGradeValue());
            stmt.setString(2, grade.getStudentAm());
            stmt.setString(3, grade.getCourseId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to update grade", ex);
        }
    }

    /**
     * Deletes the grade matching this student and course.
     */
    public void delete(Grade grade)
    {
        String sql = "DELETE FROM grades WHERE student_am = ? AND course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, grade.getStudentAm());
            stmt.setString(2, grade.getCourseId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to delete grade", ex);
        }
    }
}
