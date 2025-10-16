package com.example.bankcards.dto;

import com.example.bankcards.entity.Card.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/*
 * Объект-контейнер с параметрами пагинации и поиска чтобы 
 * Чтобы не создавать много параметров в методах контроллера и сервиса, делаем обёртку
 * */
@Setter
@Getter
@AllArgsConstructor
public class CardSearchRequest {
    private String panLast4; // последние 4 цифры
    private CardStatus status;   // фильтр по статусу (ACTIVE, BLOCKED и т.д.)
    private int page = 0;    // номер страницы
    private int size = 10;   // размер страницы
}
