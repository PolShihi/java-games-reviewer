package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionCompanyRepository extends JpaRepository<ProductionCompany, Integer> {
    
    Optional<ProductionCompany> findByName(String name);
    
    boolean existsByName(String name);
}
