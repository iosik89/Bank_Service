package com.example.bankcards.store.repository;

import com.example.bankcards.store.entities.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByPanHash(String panHash);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    Page<Card> findByUserIdAndStatus(Long userId, Card.CardStatus status, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.last4 LIKE %:last4%")
    Page<Card> findByUserIdAndLast4Containing(
            @Param("userId") Long userId,
            @Param("last4") String last4,
            Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND LOWER(c.holderName) LIKE LOWER(CONCAT('%', :holderName, '%'))")
    Page<Card> findByUserIdAndHolderNameContainingIgnoreCase(
            @Param("userId") Long userId,
            @Param("holderName") String holderName,
            Pageable pageable);
}

