package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.request.GenreCreateRequest;
import com.project.gamereviewer.dto.response.GenreResponse;
import com.project.gamereviewer.entity.Genre;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.GenreMapper;
import com.project.gamereviewer.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {
    
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
            .map(genreMapper::toResponse)
            .toList();
    }
    
    public GenreResponse getGenreById(Integer id) {
        return genreRepository.findById(id)
            .map(genreMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Genre", id));
    }
    
    @Transactional
    public GenreResponse createGenre(GenreCreateRequest request) {
        if (genreRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Genre", "name", request.name());
        }
        
        Genre genre = genreMapper.toEntity(request);
        Genre saved = genreRepository.save(genre);
        return genreMapper.toResponse(saved);
    }
    
    @Transactional
    public GenreResponse updateGenre(Integer id, GenreCreateRequest request) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Genre", id));
        
        if (!genre.getName().equals(request.name()) && genreRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Genre", "name", request.name());
        }
        
        genre.setName(request.name());
        Genre updated = genreRepository.save(genre);
        return genreMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteGenre(Integer id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre", id);
        }
        genreRepository.deleteById(id);
    }
}
