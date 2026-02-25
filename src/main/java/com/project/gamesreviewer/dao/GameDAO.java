package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GameDAO {

    private static final Logger logger = LoggerFactory.getLogger(GameDAO.class);

    @Autowired
    private DataSource dataSource;

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
}
