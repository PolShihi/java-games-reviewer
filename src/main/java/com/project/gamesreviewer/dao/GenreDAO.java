package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreDAO implements IGenreDAO {

    private static final Logger logger = LoggerFactory.getLogger(GenreDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres";
        List<Genre> genres = new ArrayList<>();

        logger.debug("Executing findAll for genres");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                genres.add(new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }

            logger.info("Found {} genre(s)", genres.size());

        } catch (SQLException e) {
            logger.error("Error fetching genres", e);
            throw new RuntimeException("Database error while fetching genres", e);
        }

        return genres;
    }

    @Override
    public Genre findById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";

        logger.debug("Finding genre by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding genre with id: {}", id, e);
            throw new RuntimeException("Database error while finding genre", e);
        }

        return null;
    }

    @Override
    public List<Genre> findByGameId(int gameId) {
        String sql = """
            SELECT g.id, g.name
            FROM genres g
            JOIN games_genres gg ON g.id = gg.genre_id
            WHERE gg.game_id = ?
            """;
        List<Genre> genres = new ArrayList<>();

        logger.debug("Finding genres for game id: {}", gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                    ));
                }
            }

            logger.info("Found {} genre(s) for game id: {}", genres.size(), gameId);

        } catch (SQLException e) {
            logger.error("Error finding genres for game id: {}", gameId, e);
            throw new RuntimeException("Database error while finding genres for game", e);
        }

        return genres;
    }

    @Override
    public int create(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?)";

        logger.debug("Creating genre: {}", genre.name());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, genre.name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating genre failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created genre with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating genre failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                logger.warn("Duplicate genre: name={}", genre.name());
                throw new DuplicateEntryException("Жанр с таким названием уже существует", e);
            }
            logger.error("Error creating genre", e);
            throw new DatabaseException("Database error while creating genre", e);
        }
    }

    @Override
    public void update(Genre genre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";

        logger.debug("Updating genre id: {}", genre.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, genre.name());
            stmt.setInt(2, genre.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated genre id: {}, rows affected: {}", genre.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating genre id: {}", genre.id(), e);
            throw new RuntimeException("Database error while updating genre", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM genres WHERE id = ?";

        logger.debug("Deleting genre id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted genre id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting genre id: {}", id, e);
            throw new RuntimeException("Database error while deleting genre", e);
        }
    }
}
