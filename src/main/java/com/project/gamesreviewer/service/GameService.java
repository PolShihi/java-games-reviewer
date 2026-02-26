package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IGameDAO;
import com.project.gamesreviewer.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    
    @Autowired
    private IGameDAO gameDAO;
    
    public List<Game> getAllGames() {
        logger.debug("Fetching all games from service layer");
        List<Game> games = gameDAO.findAll();
        logger.debug("Returning {} games from service layer", games.size());
        return games;
    }
}
