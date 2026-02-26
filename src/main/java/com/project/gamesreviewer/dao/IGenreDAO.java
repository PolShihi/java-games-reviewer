package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.Genre;

import java.util.List;

public interface IGenreDAO {
    List<Genre> findAll();
    Genre findById(int id);
    List<Genre> findByGameId(int gameId);
    int create(Genre genre);
    void update(Genre genre);
    void delete(int id);
}
