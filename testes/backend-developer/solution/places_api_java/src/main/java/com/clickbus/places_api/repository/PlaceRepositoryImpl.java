package com.clickbus.places_api.repository;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.clickbus.places_api.exceptions.OffendingFieldException;
import com.clickbus.places_api.models.Place;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.transaction.annotation.Transactional;


public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    List<String> reserved_keywords = Arrays.asList(new String[]{"new", "edit", "search"});

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public <S extends Place> S save(S place) {

        if (place.getSlug().contains(" "))
            throw new OffendingFieldException("'slug' should NOT contain spaces");

        if (reserved_keywords.contains(place.getSlug()))
            throw new OffendingFieldException(
                "'slug' field contains a reserved keyword, which is not allowed. Should NOT be any of: " + String.join(", ", reserved_keywords)
            );

        JpaEntityInformation<Place, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(Place.class, entityManager);
        if (entityInformation.isNew(place)) {
            entityManager.persist(place);
            return place;
        } else {
            return entityManager.merge(place);
        }
    }
}