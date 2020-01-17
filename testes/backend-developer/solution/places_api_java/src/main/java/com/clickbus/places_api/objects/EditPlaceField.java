package com.clickbus.places_api.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditPlaceField {

    private String name;
    private EditPlaceData data;

}