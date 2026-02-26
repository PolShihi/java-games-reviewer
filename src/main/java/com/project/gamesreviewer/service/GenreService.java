package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IGenreDAO;
import com.project.gamesreviewer.model.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private static final Logger logger = LoggerFactory.getLogger(GenreService.class);

    @Autowired
    private IGenreDAO genreDAO;

    public List<Genre> getAllGenres() {
        logger.debug("Fetching all genres");
        List<Genre> genres = genreDAO.findAll();
        genres.sort((g1, g2) -> Integer.compare(g1.id(), g2.id()));
        return genres;
    }

    public Genre getGenreById(int id) {
        logger.debug("Fetching genre by id: {}", id);
        return genreDAO.findById(id);
    }

    public List<Genre> getGenresByGameId(int gameId) {
        logger.debug("Fetching genres for game id: {}", gameId);
        return genreDAO.findByGameId(gameId);
    }

    public int createGenre(Genre genre) {
        logger.debug("Creating genre: {}", genre.name());
        return genreDAO.create(genre);
    }

    public void updateGenre(Genre genre) {
        logger.debug("Updating genre id: {}", genre.id());
        genreDAO.update(genre);
    }

    public void deleteGenre(int id) {
        logger.debug("Deleting genre id: {}", id);
        genreDAO.delete(id);
    }
}
