package com.example.bankcards.api.dto;

import com.example.bankcards.store.entities.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/*
 * Объект-контейнер с параметрами пагинации и поиска чтобы 
 * сразу передавать параметры в сервис (@ModelAttribute)
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardSearchRequest {
    private String query; // last4 или holderName
    private Card.CardStatus status;
    private int page = 0;
    private int size = 5;
}