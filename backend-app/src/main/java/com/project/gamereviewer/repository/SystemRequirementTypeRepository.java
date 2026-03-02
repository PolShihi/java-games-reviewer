package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.SystemRequirementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRequirementTypeRepository extends JpaRepository<SystemRequirementType, Integer> {
    
    Optional<SystemRequirementType> findByName(String name);
}
