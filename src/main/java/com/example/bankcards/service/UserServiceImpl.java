package com.example.bankcards.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardSearchRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
    private final CardService cardService;
   
//    @Override
//    @Transactional(readOnly = true)
//    public BigDecimal getUserBalance(Long userId) {
//        List<Card> cards = cardService.getUserCards(userId);
//        return cards.stream()
//                .map(Card::getBalance)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }

	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getFullName());
        user.setRole(dto.getRole());
        user.setId(1L); // временный id
        return UserDto.fromEntity(user);
	}

//	@Override
//	public void deleteUser(Long id) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public Page<CardDto> getUserCards(Long id, CardSearchRequest searchRequest) {
//		// TODO Auto-generated method stub
//		return cardService.getUserCards(id, searchRequest);
//	}
}

