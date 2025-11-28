package com.example.bankcards.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.api.dto.UserDto;
import com.example.bankcards.store.entities.User;
import com.example.bankcards.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
    private final PasswordEncoder encoder;
   


	@Override
    @PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setPassword(encoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
	}
	
	@Override
    @PreAuthorize("hasRole('ADMIN')")
	public void deleteUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
		userRepository.delete(user);
	}

}

