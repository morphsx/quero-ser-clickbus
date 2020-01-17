package com.clickbus.places_api.exceptions.handlers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.clickbus.places_api.exceptions.responses.DefaultErrorResponse;

import com.clickbus.places_api.exceptions.InvalidEditObjectException;
import com.clickbus.places_api.exceptions.OffendingFieldException;
import com.clickbus.places_api.exceptions.ResourceNotFoundException;

@ControllerAdvice
class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DefaultErrorResponse> handleDataIntegrityViolationException(final DataIntegrityViolationException ex) {
        DefaultErrorResponse errorResponse = new DefaultErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DefaultErrorResponse> handleNotFoundException(final ResourceNotFoundException ex) {
        DefaultErrorResponse errorResponse = new DefaultErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidEditObjectException.class)
    public ResponseEntity<DefaultErrorResponse> handleInvalidEditObjectException(final InvalidEditObjectException ex) {
        DefaultErrorResponse errorResponse = new DefaultErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(OffendingFieldException.class)
    public ResponseEntity<DefaultErrorResponse> handleOffendingFieldException(final OffendingFieldException ex) {
        DefaultErrorResponse errorResponse = new DefaultErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}