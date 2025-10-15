package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
	
	boolean existsByPanHash(String panHash);
	
	
    
	Page<Card> findByUserId(Long userId, Pageable pageable);
    
    Page<Card> findByUserIdAndStatus(Long userId, Card.CardStatus status, Pageable pageable);
    
    Page<Card> findByUserIdAndLast4Containing(Long userId, String last4, Pageable pageable);
	
}
