package com.clickbus.places_api.security.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtResponse implements Serializable{
    /**
    *
    */
    private static final long serialVersionUID = -6390299452855197160L;

    private final String jwttoken;
}