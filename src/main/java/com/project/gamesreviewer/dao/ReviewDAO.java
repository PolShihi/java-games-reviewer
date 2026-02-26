package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDAO implements IReviewDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Review> findAll() {
        String sql = """
            SELECT r.id, r.game_id, r.media_outlet_id, r.score, r.summary,
                   mo.name AS media_outlet_name,
                   g.title AS game_title
            FROM reviews r
            JOIN media_outlets mo ON r.media_outlet_id = mo.id
            JOIN games g ON r.game_id = g.id
            ORDER BY g.title, r.score DESC
            """;
        List<Review> reviews = new ArrayList<>();

        logger.debug("Executing findAll for reviews");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }

            logger.info("Found {} review(s)", reviews.size());

        } catch (SQLException e) {
            logger.error("Error fetching reviews", e);
            throw new RuntimeException("Database error while fetching reviews", e);
        }

        return reviews;
    }

    @Override
    public Review findById(int id) {
        String sql = """
            SELECT r.id, r.game_id, r.media_outlet_id, r.score, r.summary,
                   mo.name AS media_outlet_name,
                   g.title AS game_title
            FROM reviews r
            JOIN media_outlets mo ON r.media_outlet_id = mo.id
            JOIN games g ON r.game_id = g.id
            WHERE r.id = ?
            """;

        logger.debug("Finding review by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding review with id: {}", id, e);
            throw new RuntimeException("Database error while finding review", e);
        }

        return null;
    }

    @Override
    public List<Review> findByGameId(int gameId) {
        String sql = """
            SELECT r.id, r.game_id, r.media_outlet_id, r.score, r.summary,
                   mo.name AS media_outlet_name
            FROM reviews r
            JOIN media_outlets mo ON r.media_outlet_id = mo.id
            WHERE r.game_id = ?
            ORDER BY r.score DESC
            """;
        List<Review> reviews = new ArrayList<>();

        logger.debug("Finding reviews for game id: {}", gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(new Review(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getInt("media_outlet_id"),
                        rs.getInt("score"),
                        rs.getString("summary"),
                        rs.getString("media_outlet_name")
                    ));
                }
            }

            logger.info("Found {} review(s) for game id: {}", reviews.size(), gameId);

        } catch (SQLException e) {
            logger.error("Error finding reviews for game id: {}", gameId, e);
            throw new RuntimeException("Database error while finding reviews for game", e);
        }

        return reviews;
    }

    @Override
    public Double getAverageScore(int gameId) {
        String sql = "SELECT AVG(score) AS average_score FROM reviews WHERE game_id = ?";

        logger.debug("Calculating average score for game id: {}", gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object avgScore = rs.getObject("average_score");
                    if (avgScore == null) {
                        return null;
                    }
                    if (avgScore instanceof java.math.BigDecimal) {
                        return ((java.math.BigDecimal) avgScore).doubleValue();
                    }
                    return ((Number) avgScore).doubleValue();
                }
            }

        } catch (SQLException e) {
            logger.error("Error calculating average score for game id: {}", gameId, e);
            throw new RuntimeException("Database error while calculating average score", e);
        }

        return null;
    }

    @Override
    public int create(Review review) {
        String sql = "INSERT INTO reviews (game_id, media_outlet_id, score, summary) VALUES (?, ?, ?, ?)";

        logger.debug("Creating review for game id: {}", review.gameId());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, review.gameId());
            stmt.setInt(2, review.mediaOutletId());
            stmt.setInt(3, review.score());
            stmt.setString(4, review.summary());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created review with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating review failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                logger.warn("Duplicate review: game_id={}, outlet_id={}", 
                    review.gameId(), review.mediaOutletId());
                throw new DuplicateEntryException(
                    "Обзор от этого издания на данную игру уже существует", e);
            }
            logger.error("Error creating review", e);
            throw new DatabaseException("Database error while creating review", e);
        }
    }

    @Override
    public void update(Review review) {
        String sql = "UPDATE reviews SET score = ?, summary = ? WHERE id = ?";

        logger.debug("Updating review id: {}", review.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.score());
            stmt.setString(2, review.summary());
            stmt.setInt(3, review.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated review id: {}, rows affected: {}", review.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating review id: {}", review.id(), e);
            throw new RuntimeException("Database error while updating review", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";

        logger.debug("Deleting review id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted review id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting review id: {}", id, e);
            throw new RuntimeException("Database error while deleting review", e);
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        return new Review(
            rs.getInt("id"),
            rs.getInt("game_id"),
            rs.getInt("media_outlet_id"),
            rs.getInt("score"),
            rs.getString("summary"),
            rs.getString("media_outlet_name"),
            rs.getString("game_title")
        );
    }
}
