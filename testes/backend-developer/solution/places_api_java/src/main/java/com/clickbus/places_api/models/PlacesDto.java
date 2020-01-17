package com.clickbus.places_api.models;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlacesDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1690357546558765413L;
    private List<Place> places;

}