package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    
    Optional<Genre> findByName(String name);
    
    boolean existsByName(String name);
}
