package com.clickbus.places_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEditObjectException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 4544548948656730322L;

    public InvalidEditObjectException(String message) {
        super(message);
    }

    public InvalidEditObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
