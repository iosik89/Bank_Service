package com.example.bankcards.controller;

import com.example.bankcards.config.JwtUtil;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest( CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private com.example.bankcards.repository.UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testuser");
    }
    
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void testGetMyCards() throws Exception {
        // Мокаем пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        // Мокаем сервис
        CardDto cardDto = new CardDto();
        when(cardService.getUserCards(any(Long.class), any(CardSearchRequest.class)))
                .thenReturn(new PageImpl<>(List.of(cardDto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/cards/userCards"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAllCards() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setId(2L);
        cardDto.setHolderName("Another User");
        cardDto.setPan("**** **** **** 5678");
        cardDto.setBalance(BigDecimal.valueOf(500));
        cardDto.setExpirationDate(LocalDate.now().plusYears(2));

        Page<CardDto> page = new PageImpl<>(Collections.singletonList(cardDto));

        Mockito.when(cardService.getAllCards(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/cards/allCards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].holderName").value("Another User"))
                .andExpect(jsonPath("$.content[0].balance").value(500));
    }
}

