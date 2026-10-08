package com.university.registry.dao;

import com.university.registry.exception.DataAccessException;
import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.Course;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code courses} table.
 * <p>
 * Translates between {@link Course} objects and rows in MariaDB. Contains
 * no business rules (no duplicate-checking, no cross-entity logic) - that
 * responsibility belongs to {@code CourseService}, which calls this class.
 * <p>
 * Every method borrows a {@link Connection} from the shared HikariCP pool
 * (via {@link DataSourceFactory}), uses it, and returns it automatically via
 * try-with-resources. Any {@link SQLException} is caught and rethrown as an
 * unchecked {@link DataAccessException}, so callers are not forced to
 * declare or catch raw SQL exceptions.
 */
public class CourseDAO
{
    private final DataSourceFactory dataSourceFactory;

    /**
     * @param dataSourceFactory the shared connection pool factory, built
     *                          once in {@code Main} and injected into every DAO
     */
    public CourseDAO(DataSourceFactory dataSourceFactory)
    {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Inserts a new row into {@code courses}.
     *
     * @param course the course to be inserted; assumed already validated
     *               (e.g. via its constructor) before being passed here.
     * @throws DataAccessException if the insert fails for any database reason,
     *                              including a duplicate primary key (course_id).
     */
    public void insert(Course course)
    {
        String sql = "INSERT INTO courses (course_id, course_title, course_semester) VALUES (?, ?, ?)";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCourseTitle());
            stmt.setInt(3, course.getCourseSemester());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to insert course", ex);
        }
    }

    /**
     * Looks up a single course by its course_id.
     *
     * @param courseId the Course's id (primary key)
     * @return the matching {@link Course}, or {@code null} if no row matches
     * @throws DataAccessException if the query itself fails
     */
    public Course findById(String courseId)
    {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, courseId);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return mapRowToCourse(rs);
                }
                else
                {
                    return null;
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Query to find course by id failed", ex);
        }
    }

    /**
     * Loads every row in {@code courses}.
     *
     * @return all courses currently stored, as a list (empty, never null,
     *         if the table has no rows)
     * @throws DataAccessException if the query fails
     */
    public List<Course> findAll()
    {
        String sql = "SELECT * FROM courses";
        List<Course> allCourses = new ArrayList<>();

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    allCourses.add(mapRowToCourse(rs));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to load courses", ex);
        }

        return allCourses;
    }

    /**
     * Updates every column of an existing row, matched by course_id.
     *
     * @param course the course with updated field values; {@code getCourseId()}
     *               identifies which row to update
     * @throws DataAccessException if the update fails
     */
    public void update(Course course)
    {
        String sql = "UPDATE courses SET course_title = ?, course_semester = ? WHERE course_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, course.getCourseTitle());
            stmt.setInt(2, course.getCourseSemester());
            stmt.setString(3, course.getCourseId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to update course", ex);
        }
    }

    /**
     * Deletes the row matching the given course's id.
     *
     * @param course the course to be deleted; identified by {@code getCourseId()}
     * @throws DataAccessException if the delete fails (including a foreign
     *                              key violation, if active enrollments
     *                              reference this course)
     */
    public void delete(Course course)
    {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, course.getCourseId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to delete course", ex);
        }
    }

    /**
     * Builds a {@link Course} from the current row of an open {@link ResultSet}.
     * Shared by {@link #findById} and {@link #findAll} to avoid duplicating
     * the column-reading logic (DRY principle).
     *
     * @param rs a ResultSet already positioned on a valid row (i.e. after a
     *           successful {@code rs.next()})
     * @return the Course represented by that row
     * @throws SQLException if a column can't be read
     * @throws DataAccessException if the row's data fails {@link Course}'s
     *         own validation - this should be impossible, since only already
     *         validated courses are ever inserted, so it signals corrupted
     *         data rather than a normal, expected failure
     */
    Course mapRowToCourse(ResultSet rs) throws SQLException
    {
        String courseId = rs.getString("course_id");
        String courseTitle = rs.getString("course_title");
        int courseSemester = rs.getInt("course_semester");

        try
        {
            return new Course(courseId, courseTitle, courseSemester);
        }
        catch (InvalidSemesterException ex)
        {
            throw new DataAccessException("Corrupt data in courses table. Invalid semester for courseId: " + courseId, ex);
        }
    }
}