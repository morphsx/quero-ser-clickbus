package com.clickbus.places_api.objects;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditPlaceObject {

    private List<EditPlaceField> fields;

}