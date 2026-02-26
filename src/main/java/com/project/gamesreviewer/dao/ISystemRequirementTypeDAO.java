package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.SystemRequirementType;

import java.util.List;

public interface ISystemRequirementTypeDAO {
    List<SystemRequirementType> findAll();
    SystemRequirementType findById(int id);
}
