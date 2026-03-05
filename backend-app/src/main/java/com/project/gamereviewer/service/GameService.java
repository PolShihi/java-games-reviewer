package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.filter.GameFilterDto;
import com.project.gamereviewer.dto.request.GameCreateRequest;
import com.project.gamereviewer.dto.request.GameUpdateRequest;
import com.project.gamereviewer.dto.response.GameDetailResponse;
import com.project.gamereviewer.dto.response.GameListResponse;
import com.project.gamereviewer.entity.Game;
import com.project.gamereviewer.entity.Genre;
import com.project.gamereviewer.entity.ProductionCompany;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.GameMapper;
import com.project.gamereviewer.repository.GameRepository;
import com.project.gamereviewer.repository.GenreRepository;
import com.project.gamereviewer.repository.ProductionCompanyRepository;
import com.project.gamereviewer.specification.GameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    public static final String RESOURSE_NAME = "Game";
    public static final String TITLE_AND_YEAR_DUPLICATION_EXCEPTION_MESSAGE = "Game with title '%s' and year %d already exists";
    public static final String NOT_ALL_GENRES_FOUND_EXCEPTION_MESSAGE = "Some genres not found";
    
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final ProductionCompanyRepository productionCompanyRepository;
    private final GameMapper gameMapper;
    
    public Page<GameListResponse> getAllGames(Pageable pageable) {
        return gameRepository.findAll(pageable)
            .map(gameMapper::toListResponse);
    }
    
    public Page<GameListResponse> filterGames(GameFilterDto filter, Pageable pageable) {
        Specification<Game> spec = GameSpecification.withFilters(filter);
        
        return gameRepository.findAll(spec, pageable)
            .map(gameMapper::toListResponse);
    }
    
    public GameDetailResponse getGameById(Integer id) {
        Game game = gameRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
        
        return gameMapper.toDetailResponse(game);
    }
    
    @Transactional
    public GameDetailResponse createGame(GameCreateRequest request) {
        if (gameRepository.existsByTitleAndReleaseYear(request.title(), request.releaseYear())) {
            throw new DuplicateResourceException(
                String.format(TITLE_AND_YEAR_DUPLICATION_EXCEPTION_MESSAGE, 
                    request.title(), request.releaseYear())
            );
        }
        
        Game game = gameMapper.toEntity(request);
        
        if (request.developerId() != null) {
            ProductionCompany developer = productionCompanyRepository.findById(request.developerId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductionCompanyService.RESOURSE_NAME, request.developerId()));
            game.setDeveloper(developer);
        }
        
        if (request.publisherId() != null) {
            ProductionCompany publisher = productionCompanyRepository.findById(request.publisherId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductionCompanyService.RESOURSE_NAME, request.publisherId()));
            game.setPublisher(publisher);
        }
        
        if (request.genreIds() != null && !request.genreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllById(request.genreIds());
            if (genres.size() != request.genreIds().size()) {
                throw new ResourceNotFoundException(NOT_ALL_GENRES_FOUND_EXCEPTION_MESSAGE);
            }
            game.setGenres(new HashSet<>(genres));
        }
        
        Game saved = gameRepository.save(game);
        return getGameById(saved.getId());
    }

    private void setGameTitle(Game game, GameUpdateRequest request) {
        if (request.title() == null) {
            return;
        }

        if (!game.getTitle().equals(request.title()) || 
            (request.releaseYear() != null && !game.getReleaseYear().equals(request.releaseYear()))) {
            
            Integer yearToCheck = request.releaseYear() != null ? request.releaseYear() : game.getReleaseYear();
            if (gameRepository.existsByTitleAndReleaseYear(request.title(), yearToCheck)) {
                throw new DuplicateResourceException(
                    String.format(TITLE_AND_YEAR_DUPLICATION_EXCEPTION_MESSAGE, 
                        request.title(), yearToCheck)
                );
            }
        }

        game.setTitle(request.title());
    }

    private void setGameGenres(Game game, GameUpdateRequest request) {
        if (request.genreIds() == null) {
            return;
        }

        if (request.genreIds().isEmpty()) {
            game.getGenres().clear();
            return;
        } 
            
        List<Genre> genres = genreRepository.findAllById(request.genreIds());
        if (genres.size() != request.genreIds().size()) {
            throw new ResourceNotFoundException(NOT_ALL_GENRES_FOUND_EXCEPTION_MESSAGE);
        }

        game.setGenres(new HashSet<>(genres));
    }
    
    @Transactional
    public GameDetailResponse updateGame(Integer id, GameUpdateRequest request) {
        Game game = gameRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
        
        setGameTitle(game, request);
        
        if (request.releaseYear() != null) {
            game.setReleaseYear(request.releaseYear());
        }
        
        if (request.description() != null) {
            game.setDescription(request.description());
        }
        
        if (request.developerId() != null) {
            ProductionCompany developer = productionCompanyRepository.findById(request.developerId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductionCompanyService.RESOURSE_NAME, request.developerId()));
            game.setDeveloper(developer);
        }
        
        if (request.publisherId() != null) {
            ProductionCompany publisher = productionCompanyRepository.findById(request.publisherId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductionCompanyService.RESOURSE_NAME, request.publisherId()));
            game.setPublisher(publisher);
        }
        
        setGameGenres(game, request);
        
        gameRepository.save(game);
        return getGameById(id);
    }
    
    @Transactional
    public void deleteGame(Integer id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURSE_NAME, id);
        }
        gameRepository.deleteById(id);
    }
}
