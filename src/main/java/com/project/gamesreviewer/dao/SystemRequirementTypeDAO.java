package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.SystemRequirementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SystemRequirementTypeDAO implements ISystemRequirementTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(SystemRequirementTypeDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<SystemRequirementType> findAll() {
        String sql = "SELECT id, name FROM system_requirement_types ORDER BY id";
        List<SystemRequirementType> types = new ArrayList<>();

        logger.debug("Executing findAll for system requirement types");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(new SystemRequirementType(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }

            logger.info("Found {} system requirement type(s)", types.size());

        } catch (SQLException e) {
            logger.error("Error fetching system requirement types", e);
            throw new RuntimeException("Database error while fetching system requirement types", e);
        }

        return types;
    }

    @Override
    public SystemRequirementType findById(int id) {
        String sql = "SELECT id, name FROM system_requirement_types WHERE id = ?";

        logger.debug("Finding system requirement type by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SystemRequirementType(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding system requirement type with id: {}", id, e);
            throw new RuntimeException("Database error while finding system requirement type", e);
        }

        return null;
    }
}
