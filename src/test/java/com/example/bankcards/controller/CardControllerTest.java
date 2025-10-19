package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Card.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private ObjectMapper objectMapper = new ObjectMapper();
    
    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // <--- вот это
    }

    @Test
    void testCreateCard_success() throws Exception {
        // Создаем объект Card, который вернет сервис (тип должен совпадать с возвращаемым сервисом)
        // создаём DTO
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

        // Мок сервиса
        when(cardService.createCard(any(CardDto.class))).thenReturn(card);

        mockMvc.perform(post("/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holderName").value("ruslan"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
