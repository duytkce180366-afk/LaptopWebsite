package com.mycompany.techstore.Exceptions;

public class BackOfficeValidationException extends IllegalArgumentException {

    public BackOfficeValidationException(String message) {
        super(message);
    }
}