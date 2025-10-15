package com.example.bankcards.dto;

import lombok.Data;

/*
 * Объект-контейнер для всех параметров поиска и пагинации
 * */
@Data
public class CardSearchRequest {
    private String panLast4; // последние 4 цифры
    private String status;   // фильтр по статусу (ACTIVE, BLOCKED и т.д.)
    private int page = 0;    // номер страницы
    private int size = 10;   // размер страницы
}
