package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer>, JpaSpecificationExecutor<Game> {
    
    Optional<Game> findByTitleAndReleaseYear(String title, Integer releaseYear);
    
    boolean existsByTitleAndReleaseYear(String title, Integer releaseYear);
    
    @Query("SELECT COALESCE(AVG(r.score), 0.0) FROM Review r WHERE r.game.id = :gameId")
    Double calculateAverageRating(@Param("gameId") Integer gameId);
}
