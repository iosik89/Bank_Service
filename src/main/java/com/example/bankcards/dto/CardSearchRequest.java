package com.example.bankcards.dto;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.bankcards.entity.Card.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
 * Объект-контейнер с параметрами пагинации и поиска чтобы 
 * Чтобы сразу передавать параметры в сервис (@ModelAttribute)
 * */
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardSearchRequest {
    private String panLast4; // последние 4 цифры
    private CardStatus status;   // фильтр по статусу (ACTIVE, BLOCKED и т.д.)
    private int page = 0;    // номер страницы
    private int size = 10;   // размер страницы
}
