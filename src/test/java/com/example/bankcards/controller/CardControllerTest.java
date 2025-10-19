package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CardController cardController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void testCreateCard_success() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setPan("1234 5678 9012 3456");
        cardDto.setHolderName("ruslan");
        cardDto.setExpirationDate(LocalDate.of(2025, 12, 31));
        cardDto.setStatus(Card.CardStatus.ACTIVE);
        cardDto.setBalance(BigDecimal.valueOf(1000));
        cardDto.setUserId(3L);

        Card card = cardDto.toEntity();
        card.setId(1L);
        User user = new User();
        user.setId(3L);
        user.setUsername("ruslan");
        card.setUser(user);

        when(cardService.createCard(any(CardDto.class))).thenReturn(card);

        mockMvc.perform(post("/cards/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("ruslan"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetMyCards_noFilters_returnsAllCards() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("ruslan");

        when(authentication.getName()).thenReturn("ruslan");
        when(userRepository.findByUsername("ruslan")).thenReturn(java.util.Optional.of(user));

        CardDto cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setHolderName("ruslan");
        cardDto.setBalance(BigDecimal.valueOf(500));
        cardDto.setStatus(Card.CardStatus.ACTIVE);

        Page<CardDto> page = new PageImpl<>(List.of(cardDto));
        when(cardService.getUserCards(user.getId(), 0, 5)).thenReturn(page);

        mockMvc.perform(get("/cards/my")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].holderName").value("ruslan"))
                .andExpect(jsonPath("$.content[0].balance").value(500));
    }

    @Test
    void testGetCardBalance_success() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("ruslan");

        when(authentication.getName()).thenReturn("ruslan");
        when(userRepository.findByUsername("ruslan")).thenReturn(java.util.Optional.of(user));
        when(cardService.getCardBalance(1L, 3L)).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/cards/1/balance")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    void testTransferBetweenOwnCards_success() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("ruslan");

        when(authentication.getName()).thenReturn("ruslan");
        when(userRepository.findByUsername("ruslan")).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/cards/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "500")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод выполнен успешно"));
    }

    @Test
    void testGetAllCards_success() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setId(1L);
        cardDto.setHolderName("ruslan");
        cardDto.setBalance(BigDecimal.valueOf(500));
        cardDto.setStatus(Card.CardStatus.ACTIVE);

        Page<CardDto> page = new PageImpl<>(List.of(cardDto));
        when(cardService.getAllCards(PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/cards/allCards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].holderName").value("ruslan"));
    }

    @Test
    void testRequestBlock_success() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("ruslan");

        when(authentication.getName()).thenReturn("ruslan");
        when(userRepository.findByUsername("ruslan")).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/cards/1/request-block")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Блокировка карты запрошена"));
    }

    @Test
    void testBlockCard_success() throws Exception {
        mockMvc.perform(post("/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card blocked"));
    }
}
