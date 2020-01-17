package com.clickbus.places_api.repository;

import java.util.Optional;

import com.clickbus.places_api.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}