package com.clickbus.places_api.services;

import java.util.Arrays;
import java.util.List;

import com.clickbus.places_api.exceptions.OffendingFieldException;
import com.clickbus.places_api.models.Place;
import com.clickbus.places_api.repository.PlaceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final List<String> reserved_keywords = Arrays.asList(new String[]{"new", "edit", "search"});

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place save(Place place) {
        
        if (place.getSlug().contains(" "))
            throw new OffendingFieldException("'slug' should NOT contain spaces");

        if (reserved_keywords.contains(place.getSlug()))
            throw new OffendingFieldException(
                "'slug' field contains a reserved keyword, which is not allowed. Should NOT be any of: " + String.join(", ", reserved_keywords)
            );

        return placeRepository.save(place);
    }

}