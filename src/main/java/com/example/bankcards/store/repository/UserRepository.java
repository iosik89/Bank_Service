package com.example.bankcards.store.repository;


import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.store.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {
	Optional<User> findByUsername(String username);


}
