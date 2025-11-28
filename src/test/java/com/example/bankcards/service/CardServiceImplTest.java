package com.example.bankcards.service;

import com.example.bankcards.api.dto.CardDto;
import com.example.bankcards.api.dto.CardSearchRequest;
import com.example.bankcards.store.entities.Card;
import com.example.bankcards.store.entities.User;
import com.example.bankcards.exception.DuplicateCardException;
import com.example.bankcards.store.repository.CardRepository;
import com.example.bankcards.store.repository.UserRepository;
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
import static org.mockito.MockitoAnnotations.openMocks;

class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    private Card testCard;

    @BeforeEach
    void setUp() {
        openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");

        testCard = new Card();
        testCard.setUser(testUser);
        testCard.setStatus(Card.CardStatus.ACTIVE);
        testCard.setBalance(new BigDecimal(1000.00));


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
        Pageable pageable = PageRequest.of(0, 10);

        // подготавливаем мок, чтобы репозиторий возвращал Page
        List<Card> cards = List.of(new Card());
        Page<Card> page = new PageImpl<>(cards, pageable, cards.size());

        Mockito.when(cardRepository.findByUserId(1L, pageable))
                .thenReturn(page);

        // вызов метода
        var result = cardService.getUserCards(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
