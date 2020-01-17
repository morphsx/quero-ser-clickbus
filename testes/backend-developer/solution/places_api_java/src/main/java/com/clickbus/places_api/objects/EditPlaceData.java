package com.clickbus.places_api.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditPlaceData {

    private String current_value;
    private String new_value;

}