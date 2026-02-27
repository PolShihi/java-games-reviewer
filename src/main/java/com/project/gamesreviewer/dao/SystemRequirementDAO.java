package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.exception.DatabaseException;
import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.SystemRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SystemRequirementDAO implements ISystemRequirementDAO {

    private static final Logger logger = LoggerFactory.getLogger(SystemRequirementDAO.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<SystemRequirement> findAll() {
        String sql = """
            SELECT sr.id, sr.game_id, sr.system_requirement_type_id, sr.storage_gb, sr.ram_gb,
                   sr.cpu_ghz, sr.gpu_tflops, sr.vram_gb,
                   srt.name AS requirement_type,
                   g.title AS game_title
            FROM system_requirements sr
            JOIN system_requirement_types srt ON sr.system_requirement_type_id = srt.id
            JOIN games g ON sr.game_id = g.id
            ORDER BY g.title, srt.id
            """;
        List<SystemRequirement> requirements = new ArrayList<>();

        logger.debug("Executing findAll for system requirements");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requirements.add(mapResultSetToRequirement(rs));
            }

            logger.info("Found {} system requirement(s)", requirements.size());

        } catch (SQLException e) {
            logger.error("Error fetching system requirements", e);
            throw new RuntimeException("Database error while fetching system requirements", e);
        }

        return requirements;
    }

    @Override
    public SystemRequirement findById(int id) {
        String sql = """
            SELECT sr.id, sr.game_id, sr.system_requirement_type_id, sr.storage_gb, sr.ram_gb,
                   sr.cpu_ghz, sr.gpu_tflops, sr.vram_gb,
                   srt.name AS requirement_type,
                   g.title AS game_title
            FROM system_requirements sr
            JOIN system_requirement_types srt ON sr.system_requirement_type_id = srt.id
            JOIN games g ON sr.game_id = g.id
            WHERE sr.id = ?
            """;

        logger.debug("Finding system requirement by id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequirement(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding system requirement with id: {}", id, e);
            throw new RuntimeException("Database error while finding system requirement", e);
        }

        return null;
    }

    @Override
    public List<SystemRequirement> findByGameId(int gameId) {
        String sql = """
            SELECT sr.id, sr.game_id, sr.system_requirement_type_id, sr.storage_gb, sr.ram_gb,
                   sr.cpu_ghz, sr.gpu_tflops, sr.vram_gb,
                   srt.name AS requirement_type
            FROM system_requirements sr
            JOIN system_requirement_types srt ON sr.system_requirement_type_id = srt.id
            WHERE sr.game_id = ?
            ORDER BY srt.id
            """;
        List<SystemRequirement> requirements = new ArrayList<>();

        logger.debug("Finding system requirements for game id: {}", gameId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object cpuGhzObj = rs.getObject("cpu_ghz");
                    Double cpuGhz = cpuGhzObj != null ? ((Number) cpuGhzObj).doubleValue() : null;
                    
                    Object gpuTflopsObj = rs.getObject("gpu_tflops");
                    Double gpuTflops = gpuTflopsObj != null ? ((Number) gpuTflopsObj).doubleValue() : null;
                    
                    requirements.add(new SystemRequirement(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getInt("system_requirement_type_id"),
                        rs.getInt("storage_gb"),
                        rs.getInt("ram_gb"),
                        cpuGhz,
                        gpuTflops,
                        (Integer) rs.getObject("vram_gb"),
                        rs.getString("requirement_type")
                    ));
                }
            }

            logger.info("Found {} system requirement(s) for game id: {}", requirements.size(), gameId);

        } catch (SQLException e) {
            logger.error("Error finding system requirements for game id: {}", gameId, e);
            throw new RuntimeException("Database error while finding system requirements for game", e);
        }

        return requirements;
    }

    @Override
    public int create(SystemRequirement requirement) {
        String sql = "INSERT INTO system_requirements (game_id, system_requirement_type_id, storage_gb, ram_gb, cpu_ghz, gpu_tflops, vram_gb) VALUES (?, ?, ?, ?, ?, ?, ?)";

        logger.debug("Creating system requirement for game id: {}", requirement.gameId());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, requirement.gameId());
            stmt.setInt(2, requirement.systemRequirementTypeId());
            stmt.setInt(3, requirement.storageGb());
            stmt.setInt(4, requirement.ramGb());
            stmt.setObject(5, requirement.cpuGhz());
            stmt.setObject(6, requirement.gpuTflops());
            stmt.setObject(7, requirement.vramGb());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating system requirement failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    logger.info("Created system requirement with id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Creating system requirement failed, no ID obtained");
                }
            }

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null) {
                if (sqlState.equals("23505")) {
                    logger.warn("Duplicate system requirement: game_id={}, type_id={}", 
                        requirement.gameId(), requirement.systemRequirementTypeId());
                    throw new DuplicateEntryException(
                        "Системное требование с таким профилем уже существует для этой игры", e);
                } else if (sqlState.equals("23503")) {
                    logger.warn("Foreign key violation in system requirement creation: game_id={}, type_id={}", 
                        requirement.gameId(), requirement.systemRequirementTypeId());
                    throw new ForeignKeyViolationException(
                        "Указан несуществующий ID игры или типа требования", e);
                }
            }
            logger.error("Error creating system requirement", e);
            throw new DatabaseException("Database error while creating system requirement", e);
        }
    }

    @Override
    public void update(SystemRequirement requirement) {
        String sql = "UPDATE system_requirements SET storage_gb = ?, ram_gb = ?, cpu_ghz = ?, gpu_tflops = ?, vram_gb = ? WHERE id = ?";

        logger.debug("Updating system requirement id: {}", requirement.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requirement.storageGb());
            stmt.setInt(2, requirement.ramGb());
            stmt.setObject(3, requirement.cpuGhz());
            stmt.setObject(4, requirement.gpuTflops());
            stmt.setObject(5, requirement.vramGb());
            stmt.setInt(6, requirement.id());

            int affectedRows = stmt.executeUpdate();
            logger.info("Updated system requirement id: {}, rows affected: {}", requirement.id(), affectedRows);

        } catch (SQLException e) {
            logger.error("Error updating system requirement id: {}", requirement.id(), e);
            throw new RuntimeException("Database error while updating system requirement", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM system_requirements WHERE id = ?";

        logger.debug("Deleting system requirement id: {}", id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted system requirement id: {}, rows affected: {}", id, affectedRows);

        } catch (SQLException e) {
            logger.error("Error deleting system requirement id: {}", id, e);
            throw new RuntimeException("Database error while deleting system requirement", e);
        }
    }

    private SystemRequirement mapResultSetToRequirement(ResultSet rs) throws SQLException {
        Object cpuGhzObj = rs.getObject("cpu_ghz");
        Double cpuGhz = cpuGhzObj != null ? ((Number) cpuGhzObj).doubleValue() : null;
        
        Object gpuTflopsObj = rs.getObject("gpu_tflops");
        Double gpuTflops = gpuTflopsObj != null ? ((Number) gpuTflopsObj).doubleValue() : null;
        
        return new SystemRequirement(
            rs.getInt("id"),
            rs.getInt("game_id"),
            rs.getInt("system_requirement_type_id"),
            rs.getInt("storage_gb"),
            rs.getInt("ram_gb"),
            cpuGhz,
            gpuTflops,
            (Integer) rs.getObject("vram_gb"),
            rs.getString("requirement_type"),
            rs.getString("game_title")
        );
    }
}
