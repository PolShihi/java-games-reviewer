package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer>, JpaSpecificationExecutor<Game> {
    
    Optional<Game> findByTitleAndReleaseYear(String title, Integer releaseYear);
    
    boolean existsByTitleAndReleaseYear(String title, Integer releaseYear);

    @EntityGraph(attributePaths = {
        "genres",
        "reviews",
        "systemRequirements"
    })
    Optional<Game> findById(Integer id);
}
