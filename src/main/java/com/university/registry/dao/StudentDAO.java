package com.university.registry.dao;

import com.university.registry.exception.DataAccessException;
import com.university.registry.exception.InvalidSemesterException;
import com.university.registry.model.Student;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code students} table.
 * <p>
 * Translates between {@link Student} objects and rows in MariaDB. Contains
 * no business rules (no duplicate-checking, no cross-entity logic) - that
 * responsibility belongs to {@code StudentService}, which calls this class.
 * <p>
 * Every method borrows a {@link Connection} from the shared HikariCP pool
 * (via {@link DataSourceFactory}), uses it, and returns it automatically via
 * try-with-resources. Any {@link SQLException} is caught and rethrown as an
 * unchecked {@link DataAccessException}, so callers are not forced to
 * declare or catch raw SQL exceptions.
 */
public class StudentDAO
{
    private final DataSourceFactory dataSourceFactory;

    /**
     *
     * @param dataSourceFactory is the shared connection pool factory, built
     *                          once in {@code Main} and injected into every DAO
     */
    public StudentDAO(DataSourceFactory dataSourceFactory)
    {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Inserts a new row into {@code students}.
     *
     * @param student the student to be inserted assumed already validated (e.g. via its constructor) before being passed here.
     *
     * @throws DataAccessException if the insert fails for any database reason, including a duplicate primary key (am).
     */
    public void insert(Student student)
    {
        String sql = "INSERT INTO students (am, name, email, phone, semester) VALUES (?, ?, ?, ?, ?)";

        // asks that specific Connection to prepare the SQL text, with its five ? placeholders, for execution. The returned PreparedStatement is now tied to that one connection.
        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, student.getAm());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setInt(5, student.getSemester());
            stmt.executeUpdate(); // executeUpdate = for INSERT, returns rows affected
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to insert student", ex);
        }
    }

    /**
     * Looks up a single student by its am.
     *
     * @param am the Student's am (primary key)
     * @return the matching {@link Student}, or {@code null} if no row matches
     * @throws DataAccessException if the query itself fails
     */
    public Student findByAM(String am)
    {
        String sql = "SELECT * FROM students WHERE am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, am);

            try(ResultSet rs = stmt.executeQuery()) // executeQuery = for SELECT, return a ResultSet
            {
                if (rs.next()) // moves the cursor forward one row and reports whether a row exists there, false if no rows matched
                {
                    return mapRowToStudent(rs);
                }
                else
                {
                    return null;
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Query to find student by am failed", ex);
        }
    }

    /**
     * Loads every row in {@code students}.
     *
     * @return all students currently stored, as a list (if the table has no rows, method will return empty list)
     * @throws DataAccessException if the query fails
     */
    public List<Student> findAll()
    {
        String sql = "SELECT * FROM students";
        List<Student> allStudents = new ArrayList<>();

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            try(ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    allStudents.add(mapRowToStudent(rs));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to load students", ex);
        }

        return allStudents;
    }

    /**
     * Updates every column of an existing row, matched by am.
     *
     * @param student the student to be updated; {@code getAM()} identifies which row to update
     * @throws DataAccessException if the update fails
     */
    public void update(Student student)
    {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, semester = ? WHERE am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setInt(4, student.getSemester());
            stmt.setString(5, student.getAm());
            stmt.executeUpdate(); // executeUpdate = for UPDATE, returns rows affected
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to update student", ex);
        }
    }

    /**
     * Deletes the row matching the given student's AM.
     *
     * @param student the student to be deleted; identified by {@code getAM()}
     * @throws DataAccessException if the delete fails (including a foreign
     *                             key violation, if active enrollments
     *                             reference this student)
     */
    public void delete(Student student)
    {
        String sql = "DELETE FROM students WHERE am = ?";

        try(Connection conn = dataSourceFactory.getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, student.getAm());
            stmt.executeUpdate(); // executeUpdate = for DELETE, returns rows affected
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to delete student", ex);
        }
    }

    /**
     * Builds a {@link Student} from the current row of an open {@link ResultSet}.
     * Shared by {@link #findByAM} and {@link #findAll} to avoid duplicating
     * the column-reading logic (DRY principle).
     *
     * @param rs a ResultSet which will get the data of a valid row (i.e. after a successful {@code rs.next()})
     * @return the Student represented by that row
     * @throws SQLException if a column can't be read
     * @throws DataAccessException if the row's data fails the {@link Student}'s
     *         validation - this should be impossible, since only already validated
     *         students are ever inserted, so it signals corrupted data rather than
     *         a normal, expected failure.
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException
    {
        String am = rs.getString("am");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        int semester = rs.getInt("semester");

        try
        {
            return new Student(am, semester, name, email, phone);
        }
        catch (InvalidSemesterException ex)
        {
            throw new DataAccessException("Corrupt data in students table: invalid semester for am: " + am, ex); // fail fast, right at the source principle

            /*
            A catch block that always throws doesn't need to return anything.
            When a method throws, execution never reaches what would have come after,
            so the compiler doesn't require a return on that path.

            If there would be no exception to catch, the execution would have stopped
            at the return new Student inside the try block anyway. If we reach the catch block,
            it means the program failed and there is no need to return something at that point.
             */
        }
    }
}
