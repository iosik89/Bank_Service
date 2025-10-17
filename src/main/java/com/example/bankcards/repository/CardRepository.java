package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
	
	boolean existsByPanHash(String panHash);
		
	/*
	 * 
	 * */
	List<Card> findCardByUserId(Long userId);
	
	Page<Card> findByUserId(Long userId, Pageable pageable);
    
    Page<Card> findByUserIdAndStatus(Long userId, Card.CardStatus status, Pageable pageable);
    
    Page<Card> findByUserIdAndLast4Containing(Long userId, String last4, Pageable pageable);

	Page<Card> findByUserIdAndHolderNameContainingIgnoreCase(Long userId, String query, PageRequest pageable);
	
}
