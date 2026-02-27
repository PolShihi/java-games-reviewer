package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.MediaOutlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MediaOutletDAO implements IMediaOutletDAO {

    private static final Logger logger = LoggerFactory.getLogger(MediaOutletDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<MediaOutlet> findAll() {
        String sql = "SELECT id, name, website_url, founded_year FROM media_outlets";
        List<MediaOutlet> outlets = new ArrayList<>();

        logger.debug("Executing findAll for media outlets");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                outlets.add(new MediaOutlet(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("website_url"),
                    (Integer) rs.getObject("founded_year")
                ));
            }

            logger.info("Found {} media outlet(s)", outlets.size());

        } catch (SQLException e) {
            logger.error("Error fetching media outlets", e);
            throw new RuntimeException("Database error while fetching media outlets", e);
        }

        return outlets;
    }

    @Override
    public MediaOutlet findById(int id) {
        String sql = "SELECT id, name, website_url, founded_year FROM media_outlets WHERE id = ?";

        logger.debug("Finding media outlet by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MediaOutlet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("website_url"),
                        (Integer) rs.getObject("founded_year")
                    );
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding media outlet with id: {}", id, e);
            throw new RuntimeException("Database error while finding media outlet", e);
        }

        return null;
    }

    @Override
    public int create(MediaOutlet outlet) {
        String sql = "INSERT INTO media_outlets (name, website_url, founded_year) VALUES (?, ?, ?)";

        logger.debug("Creating media outlet: {}", outlet.name());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, outlet.name());
            stmt.setString(2, outlet.websiteUrl());
            stmt.setObject(3, outlet.foundedYear());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating media outlet failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created media outlet with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating media outlet failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null) {
                if (sqlState.equals("23505")) {
                    logger.warn("Duplicate media outlet: name={}", outlet.name());
                    throw new DuplicateEntryException("Издание с таким названием уже существует", e);
                }
            }
            logger.error("Error creating media outlet", e);
            throw new DatabaseException("Database error while creating media outlet", e);
        }
    }

    @Override
    public void update(MediaOutlet outlet) {
        String sql = "UPDATE media_outlets SET name = ?, website_url = ?, founded_year = ? WHERE id = ?";

        logger.debug("Updating media outlet id: {}", outlet.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, outlet.name());
            stmt.setString(2, outlet.websiteUrl());
            stmt.setObject(3, outlet.foundedYear());
            stmt.setInt(4, outlet.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated media outlet id: {}, rows affected: {}", outlet.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating media outlet id: {}", outlet.id(), e);
            throw new RuntimeException("Database error while updating media outlet", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM media_outlets WHERE id = ?";

        logger.debug("Deleting media outlet id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted media outlet id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting media outlet id: {}", id, e);
            throw new RuntimeException("Database error while deleting media outlet", e);
        }
    }
}
