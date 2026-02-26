package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.SystemRequirement;

import java.util.List;

public interface ISystemRequirementDAO {
    List<SystemRequirement> findAll();
    SystemRequirement findById(int id);
    List<SystemRequirement> findByGameId(int gameId);
    int create(SystemRequirement requirement);
    void update(SystemRequirement requirement);
    void delete(int id);
}
