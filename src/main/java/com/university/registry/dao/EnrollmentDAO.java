package com.university.registry.dao;

import com.university.registry.exception.DataAccessException;
import com.university.registry.model.Course;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code student_courses} and
 * {@code professor_courses} join tables.
 * <p>
 * Each method here replaces a specific in-memory loop that used to live in
 * {@code EnrollmentService}, back when enrollments/assignments were held in
 * a plain {@code List}. The Service methods themselves (e.g.
 * {@code assignCourseToStudent}, {@code deleteStudentSafely}) keep the same
 * shape; only the mechanism behind each check changes, from scanning a
 * {@code List} to querying the database.
 */
public class EnrollmentDAO
{
    private final DataSourceFactory dataSourceFactory;
    private final CourseDAO courseDAO;

    /**
     * @param dataSourceFactory the shared connection pool factory
     * @param courseDAO used to reuse {@code mapRowToCourse} rather than
     *                  duplicating row-to-object mapping logic here
     */
    public EnrollmentDAO(DataSourceFactory dataSourceFactory, CourseDAO courseDAO)
    {
        this.dataSourceFactory = dataSourceFactory;
        this.courseDAO = courseDAO;
    }

    /** Inserts a new row into {@code student_courses}. */
    public void enrollStudent(String am, String courseId)
    {
        String sql = "INSERT INTO student_courses (student_am, course_id) VALUES (?, ?)";

        try(Connection connection = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, am);
            stmt.setString(2, courseId);
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to enroll student", ex);
        }
    }

    /** Inserts a new row into {@code professor_courses}. */
    public void assignProfessor(String profId, String courseId)
    {
        String sql = "INSERT INTO professor_courses (prof_id, course_id) VALUES (?, ?)";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profId);
            stmt.setString(2, courseId);
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to assign professor", ex);
        }
    }

    /**
     * Replaces the duplicate-check loop inside
     * {@code EnrollmentService.assignCourseToStudent}.
     */
    public boolean isStudentEnrolled(String am, String courseId)
    {
        String sql = "SELECT COUNT(*) FROM student_courses WHERE student_am = ? AND course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, am);
            stmt.setString(2, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next(); // moves the cursor forward one row and reports whether a row exists there, false if no rows matched
                int count = rs.getInt(1); // it reads column number 1 (1-indexed) of whatever row the cursor is currently sitting on

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if the student is enrolled", ex);
        }

        /*
        For a COUNT(*) query there's only ever one row total in the result, so after
        one rs.next() call, the cursor is necessarily sitting on that single row,
        and getInt(1) reads its single column.
        getInt() = current row, column N
         */
    }

    /**
     * Replaces the duplicate-check loop inside
     * {@code EnrollmentService.assignCourseToProfessor}.
     */
    public boolean isProfessorAssigned(String profId, String courseId)
    {
        String sql = "SELECT COUNT(*) FROM professor_courses WHERE prof_id = ? AND course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profId);
            stmt.setString(2, courseId);

            try(ResultSet resultSet = stmt.executeQuery())
            {
                resultSet.next();
                int count = resultSet.getInt(1);

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if the professor is assigned", ex);
        }
    }

    /**
     * Replaces the filter-and-fetch loop inside
     * {@code EnrollmentService.getCoursesForStudent}.
     */
    public List<Course> findCoursesForStudent(String am)
    {
        String sql = """
                     SELECT c.course_id, c.course_title, c.course_semester
                     FROM courses c
                     JOIN student_courses sc ON c.course_id = sc.course_id
                     WHERE sc.student_am = ?
                     """;

        List<Course> courses = new ArrayList<>();

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, am);

            try(ResultSet rs = stmt.executeQuery())
            {
                while(rs.next())
                {
                    courses.add(courseDAO.mapRowToCourse(rs));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Query to find courses for student failed", ex);
        }

        return courses;
    }

    /**
     * Replaces the filter-and-fetch loop inside
     * {@code EnrollmentService.getCoursesForProfessor}.
     */
    public List<Course> findCoursesForProfessor(String profId)
    {
        String sql = """
                     SELECT c.course_id, c.course_title, c.course_semester
                     FROM courses c
                     JOIN professor_courses pc ON c.course_id = pc.course_id
                     WHERE pc.prof_id = ?
                     """;

        List<Course> courses = new ArrayList<>();

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profId);

            try(ResultSet rs = stmt.executeQuery())
            {
                while(rs.next())
                {
                    courses.add(courseDAO.mapRowToCourse(rs));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Query to find courses for professors failed", ex);
        }

        return courses;
    }

    /**
     * Replaces the reference-check loop inside
     * {@code EnrollmentService.deleteStudentSafely}.
     */
    public boolean hasAnyEnrollmentForStudent(String am)
    {
        String sql = "SELECT COUNT(*) FROM student_courses WHERE student_am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, am);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if there are any enrollments for student", ex);
        }
    }

    /**
     * Replaces the reference-check loop inside
     * {@code EnrollmentService.deleteProfessorSafely}.
     */
    public boolean hasAnyAssignmentForProfessor(String profId)
    {
        String sql = "SELECT COUNT(*) FROM professor_courses WHERE prof_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if there are any assignments for professor", ex);
        }
    }

    /**
     * Replaces the first half of the reference-check inside
     * {@code EnrollmentService.deleteCourseSafely} (checks students).
     */
    public boolean hasAnyEnrollmentForCourse(String courseId)
    {
        String sql = "SELECT COUNT(*) FROM student_courses WHERE course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if there are any enrollments for course", ex);
        }
    }

    /**
     * Replaces the second half of the reference-check inside
     * {@code EnrollmentService.deleteCourseSafely} (checks professors).
     */
    public boolean hasAnyAssignmentForCourse(String courseId)
    {
        String sql = "SELECT COUNT(*) FROM professor_courses WHERE course_id = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, courseId);

            try(ResultSet rs = stmt.executeQuery())
            {
                rs.next();
                int count = rs.getInt(1);

                return count > 0;
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Couldn't verify if there are any assignments for course", ex);
        }
    }
}
