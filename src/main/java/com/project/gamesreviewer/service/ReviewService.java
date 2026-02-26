package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IReviewDAO;
import com.project.gamesreviewer.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private IReviewDAO reviewDAO;

    public List<Review> getAllReviews() {
        logger.debug("Fetching all reviews");
        List<Review> reviews = reviewDAO.findAll();
        reviews.sort((r1, r2) -> Integer.compare(r1.id(), r2.id()));
        return reviews;
    }

    public Review getReviewById(int id) {
        logger.debug("Fetching review by id: {}", id);
        return reviewDAO.findById(id);
    }

    public List<Review> getReviewsByGameId(int gameId) {
        logger.debug("Fetching reviews for game id: {}", gameId);
        return reviewDAO.findByGameId(gameId);
    }

    public Double getAverageScoreForGame(int gameId) {
        logger.debug("Calculating average score for game id: {}", gameId);
        return reviewDAO.getAverageScore(gameId);
    }

    public int createReview(Review review) {
        logger.debug("Creating review for game id: {}", review.gameId());
        return reviewDAO.create(review);
    }

    public void updateReview(Review review) {
        logger.debug("Updating review id: {}", review.id());
        reviewDAO.update(review);
    }

    public void deleteReview(int id) {
        logger.debug("Deleting review id: {}", id);
        reviewDAO.delete(id);
    }
}
