package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GameDAO implements IGameDAO {

    private static final Logger logger = LoggerFactory.getLogger(GameDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Game> findAll() {
        String sql = """
                SELECT g.id, g.title, g.release_year, g.description,
                       g.developer_id, g.publisher_id,
                       dev.name AS developer_name,
                       pub.name AS publisher_name
                FROM games g
                LEFT JOIN production_companies dev ON g.developer_id = dev.id
                LEFT JOIN production_companies pub ON g.publisher_id = pub.id
                ORDER BY g.id
                """;

        List<Game> games = new ArrayList<>();

        logger.debug("Executing findAll query");
        
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            logger.trace("SQL Query: {}", sql);

            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }

            logger.info("Successfully fetched {} game(s)", games.size());

        } catch (SQLException e) {
            logger.error("Error fetching games from database", e);
            throw new RuntimeException("Database error while fetching games", e);
        }

        return games;
    }

    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        return new Game(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getInt("release_year"),
            rs.getString("description"),
            rs.getInt("developer_id"),
            rs.getInt("publisher_id"),
            rs.getString("developer_name"),
            rs.getString("publisher_name")
        );
    }
    
    @Override
    public Game findById(int id) {
        String sql = """
            SELECT g.id, g.title, g.release_year, g.description,
                   g.developer_id, g.publisher_id,
                   dev.name AS developer_name,
                   pub.name AS publisher_name
            FROM games g
            LEFT JOIN production_companies dev ON g.developer_id = dev.id
            LEFT JOIN production_companies pub ON g.publisher_id = pub.id
            WHERE g.id = ?
            """;

        logger.debug("Finding game by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGame(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding game with id: {}", id, e);
            throw new RuntimeException("Database error while finding game", e);
        }

        return null;
    }
    
    @Override
    public int create(Game game) {
        String sql = "INSERT INTO games (title, release_year, description, developer_id, publisher_id) VALUES (?, ?, ?, ?, ?)";

        logger.debug("Creating game: {}", game.title());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.title());
            stmt.setInt(2, game.releaseYear());
            stmt.setString(3, game.description());
            stmt.setInt(4, game.developerId());
            stmt.setInt(5, game.publisherId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating game failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created game with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating game failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                logger.warn("Duplicate game: title={}, year={}", game.title(), game.releaseYear());
                throw new DuplicateEntryException(
                    "Игра с таким названием и годом выпуска уже существует", e);
            }
            logger.error("Error creating game", e);
            throw new DatabaseException("Database error while creating game", e);
        }
    }
    
    @Override
    public void update(Game game) {
        String sql = "UPDATE games SET title = ?, release_year = ?, description = ?, developer_id = ?, publisher_id = ? WHERE id = ?";

        logger.debug("Updating game id: {}", game.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.title());
            stmt.setInt(2, game.releaseYear());
            stmt.setString(3, game.description());
            stmt.setInt(4, game.developerId());
            stmt.setInt(5, game.publisherId());
            stmt.setInt(6, game.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated game id: {}, rows affected: {}", game.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating game id: {}", game.id(), e);
            throw new RuntimeException("Database error while updating game", e);
        }
    }
    
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM games WHERE id = ?";

        logger.debug("Deleting game id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted game id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting game id: {}", id, e);
            throw new RuntimeException("Database error while deleting game", e);
        }
    }

    @Override
    public void addGenreToGame(int gameId, int genreId) {
        String sql = "INSERT INTO games_genres (game_id, genre_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        
        logger.debug("Adding genre {} to game {}", genreId, gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, genreId);

            int affectedRows = stmt.executeUpdate();
            logger.info("Added genre {} to game {}, rows affected: {}", genreId, gameId, affectedRows);

        } catch (SQLException e) {
            logger.error("Error adding genre {} to game {}", genreId, gameId, e);
            throw new RuntimeException("Database error while adding genre to game", e);
        }
    }

    @Override
    public void removeGenreFromGame(int gameId, int genreId) {
        String sql = "DELETE FROM games_genres WHERE game_id = ? AND genre_id = ?";

        logger.debug("Removing genre {} from game {}", genreId, gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, genreId);

            int affectedRows = stmt.executeUpdate();
            logger.info("Removed genre {} from game {}, rows affected: {}", genreId, gameId, affectedRows);

        } catch (SQLException e) {
            logger.error("Error removing genre {} from game {}", genreId, gameId, e);
            throw new RuntimeException("Database error while removing genre from game", e);
        }
    }

    @Override
    public void updateGameGenres(int gameId, List<Integer> genreIds) {
        logger.debug("Updating genres for game id: {}", gameId);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String deleteSql = "DELETE FROM games_genres WHERE game_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, gameId);
                    deleteStmt.executeUpdate();
                }

                if (genreIds != null && !genreIds.isEmpty()) {
                    String insertSql = "INSERT INTO games_genres (game_id, genre_id) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        for (Integer genreId : genreIds) {
                            insertStmt.setInt(1, gameId);
                            insertStmt.setInt(2, genreId);
                            insertStmt.addBatch();
                        }
                        insertStmt.executeBatch();
                    }
                }

                conn.commit();
                logger.info("Updated genres for game id: {}, new genre count: {}", gameId, genreIds != null ? genreIds.size() : 0);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.error("Error updating genres for game id: {}", gameId, e);
            throw new RuntimeException("Database error while updating game genres", e);
        }
    }
}
