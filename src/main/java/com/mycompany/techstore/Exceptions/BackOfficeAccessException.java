package com.mycompany.techstore.Exceptions;

public class BackOfficeAccessException extends Exception {
    private final String redirectPath;

    public BackOfficeAccessException(String message, String redirectPath) {
        super(message);
        this.redirectPath = redirectPath;
    }

    public String getRedirectPath() {
        return redirectPath;
    }
}