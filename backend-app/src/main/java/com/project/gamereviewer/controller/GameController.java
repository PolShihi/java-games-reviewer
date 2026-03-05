package com.project.gamereviewer.controller;

import java.util.List;

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
import com.project.gamereviewer.dto.filter.GameFilterDto;
import com.project.gamereviewer.dto.request.GameCreateRequest;
import com.project.gamereviewer.dto.request.GameUpdateRequest;
import com.project.gamereviewer.dto.response.GameDetailResponse;
import com.project.gamereviewer.dto.response.GameListResponse;
import com.project.gamereviewer.dto.response.PageResponse;
import com.project.gamereviewer.service.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.GAMES)
@RequiredArgsConstructor
@Tag(name = "Games", description = "Operations related to games")
public class GameController {
    
    private final GameService gameService;
    
    @GetMapping
    @Operation(summary = "Get all games with pagination", description = "Returns paginated list of games. Sort by 'averageRating' for rating-based sorting.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved games")
    })
    public ResponseEntity<PageResponse<GameListResponse>> getAllGames(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field (id, title, releaseYear, averageRating)") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<GameListResponse> gamesPage = gameService.getAllGames(pageable);
        
        PageResponse<GameListResponse> response = PageResponse.of(gamesPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get game by ID", description = "Returns detailed information about a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved game"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<GameDetailResponse> getGameById(
            @Parameter(description = "Game ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }
    
    @GetMapping("/filter")
    @Operation(
        summary = "Filter games", 
        description = "Filter games by title, release year range, genres, developer, publisher, and average rating. Sort by 'averageRating' for rating-based sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered games")
    })
    public ResponseEntity<PageResponse<GameListResponse>> filterGames(
            @Parameter(description = "Game title (partial match, case-insensitive)") 
            @RequestParam(required = false) String title,
            
            @Parameter(description = "Minimum release year (inclusive)") 
            @RequestParam(required = false) Integer yearFrom,
            
            @Parameter(description = "Maximum release year (inclusive)") 
            @RequestParam(required = false) Integer yearTo,
            
            @Parameter(description = "List of genre IDs (game must have ALL specified genres)") 
            @RequestParam(required = false) List<Integer> genreIds,
            
            @Parameter(description = "Developer company ID") 
            @RequestParam(required = false) Integer developerId,
            
            @Parameter(description = "Publisher company ID") 
            @RequestParam(required = false) Integer publisherId,
            
            @Parameter(description = "Minimum average rating (inclusive)") 
            @RequestParam(required = false) Double ratingFrom,
            
            @Parameter(description = "Maximum average rating (inclusive)") 
            @RequestParam(required = false) Double ratingTo,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field (id, title, releaseYear, averageRating)") 
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "Sort direction (ASC/DESC)") 
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        GameFilterDto filter = new GameFilterDto(
            title, 
            yearFrom, 
            yearTo, 
            genreIds, 
            developerId, 
            publisherId,
            ratingFrom,
            ratingTo
        );
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<GameListResponse> gamesPage = gameService.filterGames(filter, pageable);
        PageResponse<GameListResponse> response = PageResponse.of(gamesPage);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create new game", description = "Creates a new game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Game created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Game already exists")
    })
    public ResponseEntity<GameDetailResponse> createGame(
            @Valid @RequestBody GameCreateRequest request
    ) {
        GameDetailResponse created = gameService.createGame(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update game", description = "Updates an existing game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Game not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate game")
    })
    public ResponseEntity<GameDetailResponse> updateGame(
            @Parameter(description = "Game ID") @PathVariable Integer id,
            @Valid @RequestBody GameUpdateRequest request
    ) {
        return ResponseEntity.ok(gameService.updateGame(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game", description = "Deletes a game by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "Game ID") @PathVariable Integer id
    ) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
