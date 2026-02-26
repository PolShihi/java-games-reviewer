package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.ISystemRequirementTypeDAO;
import com.project.gamesreviewer.model.SystemRequirementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemRequirementTypeService {

    private static final Logger logger = LoggerFactory.getLogger(SystemRequirementTypeService.class);

    @Autowired
    private ISystemRequirementTypeDAO systemRequirementTypeDAO;

    public List<SystemRequirementType> getAllSystemRequirementTypes() {
        logger.debug("Fetching all system requirement types");
        List<SystemRequirementType> types = systemRequirementTypeDAO.findAll();
        types.sort((t1, t2) -> Integer.compare(t1.id(), t2.id()));
        return types;
    }

    public SystemRequirementType getSystemRequirementTypeById(int id) {
        logger.debug("Fetching system requirement type by id: {}", id);
        return systemRequirementTypeDAO.findById(id);
    }
}
