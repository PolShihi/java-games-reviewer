package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.SystemRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemRequirementRepository extends JpaRepository<SystemRequirement, Integer> {
    
    List<SystemRequirement> findByGameId(Integer gameId);
    
    Page<SystemRequirement> findByGameId(Integer gameId, Pageable pageable);
    
    Optional<SystemRequirement> findByGameIdAndSystemRequirementTypeId(Integer gameId, Integer typeId);
    
    boolean existsByGameIdAndSystemRequirementTypeId(Integer gameId, Integer typeId);
    
    void deleteByGameId(Integer gameId);
}
