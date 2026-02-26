package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IGameDAO;
import com.project.gamesreviewer.model.Game;
import com.project.gamesreviewer.model.Genre;
import com.project.gamesreviewer.model.SystemRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {
    
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    
    @Autowired
    private IGameDAO gameDAO;
    
    @Autowired
    private GenreService genreService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private SystemRequirementService systemRequirementService;
    
    public List<Game> getAllGames() {
        logger.debug("Fetching all games from service layer");
        List<Game> games = gameDAO.findAll();
        games.sort((g1, g2) -> Integer.compare(g1.id(), g2.id()));
        logger.debug("Returning {} games from service layer", games.size());
        return games;
    }
    
    public Game getGameById(int id) {
        logger.debug("Fetching game by id: {}", id);
        return gameDAO.findById(id);
    }
    
    public int createGame(Game game) {
        logger.debug("Creating game: {}", game.title());
        return gameDAO.create(game);
    }
    
    public int createGameWithGenres(Game game, List<Integer> genreIds) {
        logger.debug("Creating game with genres: {}", game.title());
        int gameId = gameDAO.create(game);
        
        if (genreIds != null && !genreIds.isEmpty()) {
            gameDAO.updateGameGenres(gameId, genreIds);
        }
        
        logger.info("Created game with id: {} and {} genres", gameId, genreIds != null ? genreIds.size() : 0);
        return gameId;
    }
    
    public void updateGame(Game game) {
        logger.debug("Updating game id: {}", game.id());
        gameDAO.update(game);
    }
    
    public void deleteGame(int id) {
        logger.debug("Deleting game id: {}", id);
        gameDAO.delete(id);
    }
    
    public Game getGameWithFullDetails(int gameId) {
        logger.debug("Fetching full details for game id: {}", gameId);
        
        Game game = gameDAO.findById(gameId);
        if (game == null) {
            logger.warn("Game with id {} not found", gameId);
            return null;
        }
        
        List<Genre> genres = genreService.getGenresByGameId(gameId);
        List<String> genreNames = genres.stream()
                .map(Genre::name)
                .collect(Collectors.toList());
        
        Double averageRating = reviewService.getAverageScoreForGame(gameId);
        
        game = game.withGenres(genreNames).withAverageRating(averageRating);
        
        logger.info("Fetched full details for game: {} (genres: {}, rating: {})", 
                game.title(), genreNames.size(), averageRating);
        
        return game;
    }
    
    public List<SystemRequirement> getGameSystemRequirements(int gameId) {
        logger.debug("Fetching system requirements for game id: {}", gameId);
        return systemRequirementService.getRequirementsByGameId(gameId);
    }
    
    public void addGenreToGame(int gameId, int genreId) {
        logger.debug("Adding genre {} to game {}", genreId, gameId);
        gameDAO.addGenreToGame(gameId, genreId);
    }
    
    public void removeGenreFromGame(int gameId, int genreId) {
        logger.debug("Removing genre {} from game {}", genreId, gameId);
        gameDAO.removeGenreFromGame(gameId, genreId);
    }
    
    public void updateGameGenres(int gameId, List<Integer> genreIds) {
        logger.debug("Updating genres for game id: {}", gameId);
        gameDAO.updateGameGenres(gameId, genreIds);
    }
}
