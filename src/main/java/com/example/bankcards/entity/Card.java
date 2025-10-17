package com.example.bankcards.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.bankcards.util.PanAttributeConverter;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AttributeConverter для шифрования/дешифрования
    @Convert(converter = PanAttributeConverter.class)
    @Column(name = "card_pan_enc", nullable = false)
    private String pan; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "pan_hash", nullable = false, unique = true, length = 128)
    private String panHash;

    @Column(name = "last4", length = 4, nullable = false)
    private String last4;

    @Column(name = "holder_name", nullable = false, length = 200)
    private String holderName;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardStatus status; 

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "block_request", nullable = false)
    private boolean blockRequest = false;
    
    public enum CardStatus {
        ACTIVE, BLOCKED, EXPIRED
    }
}
