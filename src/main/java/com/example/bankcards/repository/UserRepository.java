package com.example.bankcards.repository;


import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {
	Optional<User> findByUsername(String username);
}
