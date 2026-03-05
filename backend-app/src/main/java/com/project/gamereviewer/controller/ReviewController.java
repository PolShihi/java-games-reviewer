package com.project.gamereviewer.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.request.ReviewCreateRequest;
import com.project.gamereviewer.dto.response.PageResponse;
import com.project.gamereviewer.dto.response.ReviewResponse;
import com.project.gamereviewer.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.REVIEWS)
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Operations related to game reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @GetMapping
    @Operation(summary = "Get all reviews", description = "Returns paginated list of all reviews")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getAllReviews(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ReviewResponse> reviewsPage = reviewService.getAllReviews(pageable);
        
        PageResponse<ReviewResponse> response = PageResponse.of(reviewsPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/game/{gameId}")
    @Operation(summary = "Get reviews by game ID", description = "Returns all reviews for a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByGameId(
            @Parameter(description = "Game ID") @PathVariable Integer gameId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
        Page<ReviewResponse> reviewsPage = reviewService.getReviewsByGameId(gameId, pageable);
        
        PageResponse<ReviewResponse> response = PageResponse.of(reviewsPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Returns a specific review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved review"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewResponse> getReviewById(
            @Parameter(description = "Review ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new review", description = "Creates a new game review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Game or Media Outlet not found"),
        @ApiResponse(responseCode = "409", description = "Review already exists for this game and outlet")
    })
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        ReviewResponse created = reviewService.createReview(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update review", description = "Updates an existing review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID") @PathVariable Integer id,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Deletes a review by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID") @PathVariable Integer id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
