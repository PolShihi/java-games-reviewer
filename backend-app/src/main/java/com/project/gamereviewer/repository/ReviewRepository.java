package com.project.gamereviewer.repository;

import com.project.gamereviewer.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    List<Review> findByGameId(Integer gameId);
    
    Page<Review> findByGameId(Integer gameId, Pageable pageable);
    
    Optional<Review> findByGameIdAndMediaOutletId(Integer gameId, Integer mediaOutletId);
    
    boolean existsByGameIdAndMediaOutletId(Integer gameId, Integer mediaOutletId);

    boolean existsByGameIdAndMediaOutletIdAndIdNot(Integer gameId, Integer mediaOutletId, Integer id);
    
    void deleteByGameId(Integer gameId);
}
