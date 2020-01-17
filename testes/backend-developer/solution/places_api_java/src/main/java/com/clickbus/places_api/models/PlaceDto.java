package com.clickbus.places_api.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlaceDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8417912718599057715L;
    private Place place;

}