package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.MediaOutlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaOutletRepository extends JpaRepository<MediaOutlet, Integer> {
    
    Optional<MediaOutlet> findByName(String name);
    
    boolean existsByName(String name);
}
