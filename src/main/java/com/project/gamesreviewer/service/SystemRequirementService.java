package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.ISystemRequirementDAO;
import com.project.gamesreviewer.model.SystemRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemRequirementService {

    private static final Logger logger = LoggerFactory.getLogger(SystemRequirementService.class);

    @Autowired
    private ISystemRequirementDAO systemRequirementDAO;

    public List<SystemRequirement> getAllRequirements() {
        logger.debug("Fetching all system requirements");
        List<SystemRequirement> requirements = systemRequirementDAO.findAll();
        requirements.sort((r1, r2) -> Integer.compare(r1.id(), r2.id()));
        return requirements;
    }

    public SystemRequirement getRequirementById(int id) {
        logger.debug("Fetching system requirement by id: {}", id);
        return systemRequirementDAO.findById(id);
    }

    public List<SystemRequirement> getRequirementsByGameId(int gameId) {
        logger.debug("Fetching system requirements for game id: {}", gameId);
        return systemRequirementDAO.findByGameId(gameId);
    }

    public int createRequirement(SystemRequirement requirement) {
        logger.debug("Creating system requirement for game id: {}", requirement.gameId());
        return systemRequirementDAO.create(requirement);
    }

    public void updateRequirement(SystemRequirement requirement) {
        logger.debug("Updating system requirement id: {}", requirement.id());
        systemRequirementDAO.update(requirement);
    }

    public void deleteRequirement(int id) {
        logger.debug("Deleting system requirement id: {}", id);
        systemRequirementDAO.delete(id);
    }
}
