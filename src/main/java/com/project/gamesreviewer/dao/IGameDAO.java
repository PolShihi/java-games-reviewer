package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.Game;

import java.util.List;

public interface IGameDAO {
    List<Game> findAll();
    Game findById(int id);
    int create(Game game);
    void update(Game game);
    void delete(int id);
}
