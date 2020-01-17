package com.clickbus.places_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OffendingFieldException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 4544548948656730322L;

    public OffendingFieldException(String message) {
        super(message);
    }

    public OffendingFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
