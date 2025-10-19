package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.DuplicateCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");
    }

    @Test
    void createCard_success() {
        CardDto dto = new CardDto();
        dto.setUserId(1L);
        dto.setPan("1234 5678 9012 3456");
        dto.setHolderName("Держатель карты");
        dto.setBalance(BigDecimal.valueOf(1000));
        dto.setExpirationDate(LocalDate.now().plusYears(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardRepository.existsByPanHash(anyString())).thenReturn(false);

        Card savedCard = new Card();
        savedCard.setId(1L);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        Card result = cardService.createCard(dto);

        assertEquals(savedCard.getId(), result.getId());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_duplicatePan_throwsException() {
        CardDto dto = new CardDto();
        dto.setUserId(1L);
        dto.setPan("1234 5678 9012 3456");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardRepository.existsByPanHash(anyString())).thenReturn(true);

        assertThrows(DuplicateCardException.class, () -> cardService.createCard(dto));
    }

    @Test
    void getCardBalance_success() {
        Card card = new Card();
        card.setId(1L);
        card.setBalance(BigDecimal.valueOf(500));
        card.setUser(testUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        BigDecimal balance = cardService.getCardBalance(1L, 1L);

        assertEquals(BigDecimal.valueOf(500), balance);
    }

    @Test
    void transferBetweenOwnCards_success() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setUser(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setUser(testUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenOwnCards(1L, 1L, 2L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(800), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());
    }

    @Test
    void getUserCards_withStatus() {
        CardSearchRequest request = new CardSearchRequest();
        request.setStatus(Card.CardStatus.ACTIVE);
        request.setPage(0);
        request.setSize(10);

        Card card = new Card();
        card.setId(1L);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findByUserIdAndStatus(1L, Card.CardStatus.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<CardDto> result = cardService.getUserCards(1L, request.getPage(),request.getSize());

        assertEquals(1, result.getTotalElements());
    }
}
