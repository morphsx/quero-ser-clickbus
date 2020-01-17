package com.clickbus.places_api.controllers;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import com.clickbus.places_api.exceptions.InvalidEditObjectException;
import com.clickbus.places_api.exceptions.ResourceNotFoundException;
import com.clickbus.places_api.models.Place;
import com.clickbus.places_api.models.PlaceDto;
import com.clickbus.places_api.models.PlacesDto;
import com.clickbus.places_api.objects.EditPlaceField;
import com.clickbus.places_api.objects.EditPlaceObject;
import com.clickbus.places_api.repository.PlaceRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1.0/places"})
public class PlaceController {

    private PlaceRepository repository;

    PlaceController(PlaceRepository placeRepository) {
        this.repository = placeRepository;
    }

    @GetMapping
    public PlacesDto listPlaces() {
        return new PlacesDto(repository.findAll());
    }

    @GetMapping(path = {"/{slug}"})
    public PlaceDto fetchPlace(@PathVariable String slug) {
        return new PlaceDto(
            repository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Place with slug: " + slug + " not found"))
        );
    }

    @GetMapping(path = {"/search/{name}"})
    public PlacesDto searchPlace(@PathVariable String name) {
        return new PlacesDto(repository.findByNameContainingIgnoreCase(name));
    }

    @PostMapping(path = {"/new"})
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceDto createPlace(@Valid @RequestBody Place place) {

        if (place.getSlug().contains(" "))
            throw new DataIntegrityViolationException("Field 'slug' should not contain spaces");

        try {
            return new PlaceDto(repository.save(place));
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("A Place with this 'slug' already exists");
        }
    }

    @PutMapping(path = {"/edit/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public PlaceDto editPlace(@PathVariable long id, @RequestBody EditPlaceObject object) {

        // Checking if informed place exists
        Place place = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Place with id: " + id + " not found"));

        List<String> allowed_fields = Arrays.asList(new String[]{"name", "slug", "city", "state"});

        // Checking if request has any field to change
        if (object.getFields().size() <= 0)
            throw new InvalidEditObjectException("No 'field' specified for change. Should be one or more of: " + String.join(", ", allowed_fields));

        for (EditPlaceField f : object.getFields()) {

            // Checking if 'fields' contain field_name and data attribute
            if (f.getName() == null || f.getData() == null)
                throw new InvalidEditObjectException("'fields' attribute should have 'name' and 'data {}' attribute");

            // Checking if field names are valid
            if (!allowed_fields.contains(f.getName()))
                throw new InvalidEditObjectException("Invalid field name: '" + f.getName() + "'. Should be one of: " + String.join(", ", allowed_fields));

            // Checking if given field contains 'current_value' and 'new_value' attributes
            if (f.getData().getCurrent_value() == null || f.getData().getNew_value() == null)
                throw new InvalidEditObjectException("'data {}' attribute should have 'current_value' and 'new_value' attributes");

            // Checking if current_value of field correspond to model instance
            String fieldWithError = "";
            String current_value = f.getData().getCurrent_value();
            String new_value = f.getData().getNew_value();

            switch (f.getName()) {
                case "name":
                    if (!place.getName().equals(current_value))
                        fieldWithError = "'name'";
                    place.setName(new_value);
                    break;
                case "slug":
                    if (!place.getSlug().equals(current_value))
                        fieldWithError = "'slug'";
                    place.setSlug(new_value);
                    break;
                case "city":
                    if (!place.getCity().equals(current_value))
                        fieldWithError = "'city'";
                    place.setCity(f.getData().getCurrent_value());
                    place.setCity(new_value);
                    break;
                case "state":
                    if (!place.getState().equals(current_value))
                        fieldWithError = "'state'";
                    place.setState(new_value);
                    break;
            }

            if (fieldWithError.length() > 0)
                throw new InvalidEditObjectException("'current_value' for field " + fieldWithError + " is incorrect");
        }

        try {
            repository.save(place);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("A Place with this slug already exists");
        }

        return new PlaceDto(place);
    }
}