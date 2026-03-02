package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType, Integer> {
    
    Optional<CompanyType> findByName(String name);
    
    boolean existsByName(String name);
}
