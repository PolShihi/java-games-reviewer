package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.ProductionCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductionCompanyDAO implements IProductionCompanyDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProductionCompanyDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<ProductionCompany> findAll() {
        String sql = """
            SELECT pc.id, pc.name, pc.founded_year, pc.website_url, pc.ceo, pc.company_type_id,
                   ct.name AS company_type_name
            FROM production_companies pc
            LEFT JOIN company_types ct ON pc.company_type_id = ct.id
            """;
        List<ProductionCompany> companies = new ArrayList<>();

        logger.debug("Executing findAll for production companies");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

            logger.info("Found {} production company(ies)", companies.size());

        } catch (SQLException e) {
            logger.error("Error fetching production companies", e);
            throw new RuntimeException("Database error while fetching production companies", e);
        }

        return companies;
    }

    @Override
    public ProductionCompany findById(int id) {
        String sql = """
            SELECT pc.id, pc.name, pc.founded_year, pc.website_url, pc.ceo, pc.company_type_id,
                   ct.name AS company_type_name
            FROM production_companies pc
            LEFT JOIN company_types ct ON pc.company_type_id = ct.id
            WHERE pc.id = ?
            """;

        logger.debug("Finding production company by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding production company with id: {}", id, e);
            throw new RuntimeException("Database error while finding production company", e);
        }

        return null;
    }

    @Override
    public int create(ProductionCompany company) {
        String sql = "INSERT INTO production_companies (name, founded_year, website_url, ceo, company_type_id) VALUES (?, ?, ?, ?, ?)";

        logger.debug("Creating production company: {}", company.name());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, company.name());
            stmt.setObject(2, company.foundedYear());
            stmt.setString(3, company.websiteUrl());
            stmt.setString(4, company.ceo());
            stmt.setObject(5, company.companyTypeId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating production company failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created production company with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating production company failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null) {
                if (sqlState.equals("23505")) {
                    logger.warn("Duplicate company: name={}", company.name());
                    throw new DuplicateEntryException("Компания с таким названием уже существует", e);
                } else if (sqlState.equals("23503")) {
                    logger.warn("Foreign key violation in company creation: type_id={}", company.companyTypeId());
                    throw new ForeignKeyViolationException(
                        "Указан несуществующий тип компании", e);
                }
            }
            logger.error("Error creating production company", e);
            throw new DatabaseException("Database error while creating production company", e);
        }
    }

    @Override
    public void update(ProductionCompany company) {
        String sql = "UPDATE production_companies SET name = ?, founded_year = ?, website_url = ?, ceo = ?, company_type_id = ? WHERE id = ?";

        logger.debug("Updating production company id: {}", company.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.name());
            stmt.setObject(2, company.foundedYear());
            stmt.setString(3, company.websiteUrl());
            stmt.setString(4, company.ceo());
            stmt.setObject(5, company.companyTypeId());
            stmt.setInt(6, company.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated production company id: {}, rows affected: {}", company.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating production company id: {}", company.id(), e);
            throw new RuntimeException("Database error while updating production company", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM production_companies WHERE id = ?";

        logger.debug("Deleting production company id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted production company id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting production company id: {}", id, e);
            throw new RuntimeException("Database error while deleting production company", e);
        }
    }

    private ProductionCompany mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new ProductionCompany(
            rs.getInt("id"),
            rs.getString("name"),
            (Integer) rs.getObject("founded_year"),
            rs.getString("website_url"),
            rs.getString("ceo"),
            (Integer) rs.getObject("company_type_id"),
            rs.getString("company_type_name")
        );
    }
}
