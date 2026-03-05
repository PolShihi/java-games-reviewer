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

    public static final String RESOURSE_NAME = "Genre";
    public static final String RESOURSE_NAME_FIELD_NAME = "name";
    
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
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
    }
    
    @Transactional
    public GenreResponse createGenre(GenreCreateRequest request) {
        if (genreRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(RESOURSE_NAME, RESOURSE_NAME_FIELD_NAME, request.name());
        }
        
        Genre genre = genreMapper.toEntity(request);
        Genre saved = genreRepository.save(genre);
        return genreMapper.toResponse(saved);
    }
    
    @Transactional
    public GenreResponse updateGenre(Integer id, GenreCreateRequest request) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
        
        if (!genre.getName().equals(request.name()) && genreRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(RESOURSE_NAME, RESOURSE_NAME_FIELD_NAME, request.name());
        }
        
        genre.setName(request.name());
        Genre updated = genreRepository.save(genre);
        return genreMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteGenre(Integer id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURSE_NAME, id);
        }
        genreRepository.deleteById(id);
    }
}
