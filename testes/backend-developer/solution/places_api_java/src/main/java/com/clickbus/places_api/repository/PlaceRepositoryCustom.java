package com.clickbus.places_api.repository;

import com.clickbus.places_api.models.Place;

public interface PlaceRepositoryCustom {
    <S extends Place> S save(S place);
}