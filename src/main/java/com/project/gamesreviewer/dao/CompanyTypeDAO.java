package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.CompanyType;
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
public class CompanyTypeDAO implements ICompanyTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompanyTypeDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<CompanyType> findAll() {
        String sql = "SELECT id, name FROM company_types";
        List<CompanyType> types = new ArrayList<>();

        logger.debug("Executing findAll for company types");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(new CompanyType(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }

            logger.info("Found {} company type(s)", types.size());

        } catch (SQLException e) {
            logger.error("Error fetching company types", e);
            throw new RuntimeException("Database error while fetching company types", e);
        }

        return types;
    }

    @Override
    public CompanyType findById(int id) {
        String sql = "SELECT id, name FROM company_types WHERE id = ?";

        logger.debug("Finding company type by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CompanyType(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding company type with id: {}", id, e);
            throw new RuntimeException("Database error while finding company type", e);
        }

        return null;
    }
}
