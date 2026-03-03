package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.request.ReviewCreateRequest;
import com.project.gamereviewer.dto.response.ReviewResponse;
import com.project.gamereviewer.entity.Game;
import com.project.gamereviewer.entity.MediaOutlet;
import com.project.gamereviewer.entity.Review;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.ReviewMapper;
import com.project.gamereviewer.repository.GameRepository;
import com.project.gamereviewer.repository.MediaOutletRepository;
import com.project.gamereviewer.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final MediaOutletRepository mediaOutletRepository;
    private final ReviewMapper reviewMapper;
    
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
            .map(reviewMapper::toResponse);
    }
    
    public Page<ReviewResponse> getReviewsByGameId(Integer gameId, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game", gameId);
        }
        return reviewRepository.findByGameId(gameId, pageable)
            .map(reviewMapper::toResponse);
    }
    
    public ReviewResponse getReviewById(Integer id) {
        return reviewRepository.findById(id)
            .map(reviewMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }
    
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        if (reviewRepository.existsByGameIdAndMediaOutletId(request.gameId(), request.mediaOutletId())) {
            throw new DuplicateResourceException(
                "This media outlet already reviewed this game"
            );
        }
        
        Game game = gameRepository.findById(request.gameId())
            .orElseThrow(() -> new ResourceNotFoundException("Game", request.gameId()));
        
        MediaOutlet mediaOutlet = mediaOutletRepository.findById(request.mediaOutletId())
            .orElseThrow(() -> new ResourceNotFoundException("MediaOutlet", request.mediaOutletId()));
        
        Review review = reviewMapper.toEntity(request);
        review.setGame(game);
        review.setMediaOutlet(mediaOutlet);
        
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }
    
    @Transactional
    public ReviewResponse updateReview(Integer id, ReviewCreateRequest request) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        
        review.setScore(request.score());
        review.setSummary(request.summary());
        
        Review updated = reviewRepository.save(review);
        return reviewMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }
}
