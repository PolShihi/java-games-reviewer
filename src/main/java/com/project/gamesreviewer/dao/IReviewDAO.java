package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.Review;

import java.util.List;

public interface IReviewDAO {
    List<Review> findAll();
    Review findById(int id);
    List<Review> findByGameId(int gameId);
    Double getAverageScore(int gameId);
    int create(Review review);
    void update(Review review);
    void delete(int id);
}
