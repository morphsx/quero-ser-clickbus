package com.clickbus.places_api.exceptions.responses;

public class DefaultErrorResponse {
    // Using this style of syntax only to keep compatibility with python implementation
    private String error_message;

    public DefaultErrorResponse(String message) {
        this.error_message = message;
    }

    public String getError_message() {
        return error_message;
    }

    public void setErrorMessage(String message) {
        this.error_message = message;
    }
}