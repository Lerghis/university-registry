package com.university.registry.dao;

import com.university.registry.exception.DataAccessException;
import com.university.registry.model.Professor;
import com.university.registry.util.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code professors} table.
 * <p>
 * Translates between {@link Professor} objects and rows in MariaDB. Contains
 * no business rules (no duplicate-checking, no cross-entity logic) - that
 * responsibility belongs to {@code ProfessorService}, which calls this class.
 * <p>
 * Every method borrows a {@link Connection} from the shared HikariCP pool
 * (via {@link DataSourceFactory}), uses it, and returns it automatically via
 * try-with-resources. Any {@link SQLException} is caught and rethrown as an
 * unchecked {@link DataAccessException}, so callers are not forced to
 * declare or catch raw SQL exceptions.
 */
public class ProfessorDAO
{
    private final DataSourceFactory dataSourceFactory;

    /**
     * @param dataSourceFactory the shared connection pool factory, built
     *                          once in {@code Main} and injected into every DAO
     */
    public ProfessorDAO(DataSourceFactory dataSourceFactory)
    {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Inserts a new row into {@code professors}.
     *
     * @param professor the professor to be inserted; assumed already validated
     *                  (e.g. via its constructor) before being passed here.
     * @throws DataAccessException if the insert fails for any database reason,
     *                              including a duplicate primary key (prof_id).
     */
    public void insert(Professor professor)
    {
        String sql = "INSERT INTO professors (prof_id, specialty, name, email, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, professor.getProfId());
            stmt.setString(2, professor.getSpecialty());
            stmt.setString(3, professor.getName());
            stmt.setString(4, professor.getEmail());
            stmt.setString(5, professor.getPhone());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to insert professor", ex);
        }
    }

    /**
     * Looks up a single professor by its prof_id.
     *
     * @param profId the Professor's id (primary key)
     * @return the matching {@link Professor}, or {@code null} if no row matches
     * @throws DataAccessException if the query itself fails
     */
    public Professor findById(String profId)
    {
        String sql = "SELECT * FROM professors WHERE prof_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, profId);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return mapRowToProfessor(rs);
                }
                else
                {
                    return null;
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Query to find professor by id failed", ex);
        }
    }

    /**
     * Loads every row in {@code professors}.
     *
     * @return all professors currently stored, as a list (empty, never null,
     *         if the table has no rows)
     * @throws DataAccessException if the query fails
     */
    public List<Professor> findAll()
    {
        String sql = "SELECT * FROM professors";
        List<Professor> allProfessors = new ArrayList<>();

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    allProfessors.add(mapRowToProfessor(rs));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to load professors", ex);
        }

        return allProfessors;
    }

    /**
     * Updates every column of an existing row, matched by prof_id.
     *
     * @param professor the professor with updated field values; {@code getProfId()}
     *                  identifies which row to update
     * @throws DataAccessException if the update fails
     */
    public void update(Professor professor)
    {
        String sql = "UPDATE professors SET specialty = ?, name = ?, email = ?, phone = ? WHERE prof_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, professor.getSpecialty());
            stmt.setString(2, professor.getName());
            stmt.setString(3, professor.getEmail());
            stmt.setString(4, professor.getPhone());
            stmt.setString(5, professor.getProfId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to update professor", ex);
        }
    }

    /**
     * Deletes the row matching the given professor's id.
     *
     * @param professor the professor to be deleted; identified by {@code getProfId()}
     * @throws DataAccessException if the delete fails (including a foreign
     *                              key violation, if active assignments
     *                              reference this professor)
     */
    public void delete(Professor professor)
    {
        String sql = "DELETE FROM professors WHERE prof_id = ?";

        try (Connection conn = dataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, professor.getProfId());
            stmt.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("Failed to delete professor", ex);
        }
    }

    /**
     * Builds a {@link Professor} from the current row of an open {@link ResultSet}.
     * Shared by {@link #findById} and {@link #findAll} to avoid duplicating
     * the column-reading logic (DRY principle).
     *
     * @param rs a ResultSet already positioned on a valid row (i.e. after a
     *           successful {@code rs.next()})
     * @return the Professor represented by that row
     * @throws SQLException if a column can't be read
     */
    private Professor mapRowToProfessor(ResultSet rs) throws SQLException
    {
        String profId = rs.getString("prof_id");
        String specialty = rs.getString("specialty");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");

        return new Professor(profId, specialty, name, email, phone);
    }
}