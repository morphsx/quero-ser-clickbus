package com.clickbus.places_api.security.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtRequest implements Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 354771234861235049L;

    private String username;
    private String password;
}