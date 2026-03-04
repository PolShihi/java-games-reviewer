package com.project.gamereviewer.controller;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.request.MediaOutletCreateRequest;
import com.project.gamereviewer.dto.response.MediaOutletResponse;
import com.project.gamereviewer.dto.response.PageResponse;
import com.project.gamereviewer.service.MediaOutletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.MEDIA_OUTLETS)
@RequiredArgsConstructor
@Tag(name = "Media Outlets", description = "Operations related to media outlets")
public class MediaOutletController {
    
    private final MediaOutletService mediaOutletService;
    
    @GetMapping
    @Operation(summary = "Get all media outlets", description = "Returns paginated list of media outlets")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved media outlets")
    public ResponseEntity<PageResponse<MediaOutletResponse>> getAllMediaOutlets(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<MediaOutletResponse> outletsPage = mediaOutletService.getAllMediaOutlets(pageable);
        
        PageResponse<MediaOutletResponse> response = PageResponse.of(outletsPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get media outlet by ID", description = "Returns a specific media outlet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved media outlet"),
        @ApiResponse(responseCode = "404", description = "Media outlet not found")
    })
    public ResponseEntity<MediaOutletResponse> getMediaOutletById(
            @Parameter(description = "Media Outlet ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(mediaOutletService.getMediaOutletById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new media outlet", description = "Creates a new media outlet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Media outlet created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Media outlet already exists")
    })
    public ResponseEntity<MediaOutletResponse> createMediaOutlet(
            @Valid @RequestBody MediaOutletCreateRequest request
    ) {
        MediaOutletResponse created = mediaOutletService.createMediaOutlet(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update media outlet", description = "Updates an existing media outlet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Media outlet updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Media outlet not found"),
        @ApiResponse(responseCode = "409", description = "Media outlet name already exists")
    })
    public ResponseEntity<MediaOutletResponse> updateMediaOutlet(
            @Parameter(description = "Media Outlet ID") @PathVariable Integer id,
            @Valid @RequestBody MediaOutletCreateRequest request
    ) {
        return ResponseEntity.ok(mediaOutletService.updateMediaOutlet(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete media outlet", description = "Deletes a media outlet by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Media outlet deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Media outlet not found")
    })
    public ResponseEntity<Void> deleteMediaOutlet(
            @Parameter(description = "Media Outlet ID") @PathVariable Integer id
    ) {
        mediaOutletService.deleteMediaOutlet(id);
        return ResponseEntity.noContent().build();
    }
}
