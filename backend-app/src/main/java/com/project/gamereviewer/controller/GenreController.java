package com.project.gamereviewer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.request.GenreCreateRequest;
import com.project.gamereviewer.dto.response.GenreResponse;
import com.project.gamereviewer.service.GenreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.GENRES)
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Operations related to game genres")
public class GenreController {
    
    private final GenreService genreService;
    
    @GetMapping
    @Operation(summary = "Get all genres", description = "Returns list of all genres")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved genres")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID", description = "Returns a specific genre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved genre"),
        @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<GenreResponse> getGenreById(
            @Parameter(description = "Genre ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new genre", description = "Creates a new genre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Genre created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Genre already exists")
    })
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @RequestBody GenreCreateRequest request
    ) {
        GenreResponse created = genreService.createGenre(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update genre", description = "Updates an existing genre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Genre updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Genre not found"),
        @ApiResponse(responseCode = "409", description = "Genre name already exists")
    })
    public ResponseEntity<GenreResponse> updateGenre(
            @Parameter(description = "Genre ID") @PathVariable Integer id,
            @Valid @RequestBody GenreCreateRequest request
    ) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre", description = "Deletes a genre by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "Genre ID") @PathVariable Integer id
    ) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
