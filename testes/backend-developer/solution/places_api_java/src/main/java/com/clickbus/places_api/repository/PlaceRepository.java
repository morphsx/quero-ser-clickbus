package com.clickbus.places_api.repository;

import java.util.List;
import java.util.Optional;

import com.clickbus.places_api.models.Place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Place> findByNameContainingIgnoreCase(String name);

}