package com.mycompany.techstore.exceptions;

public class AuthException extends java.lang.Exception {

    private static final long serialVersionUID = 1L;

    private final int code;

    public AuthException(int code, String reason) {
        super(reason);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
