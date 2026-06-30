package com.mycompany.techstore.Exceptions;

public class ProfileException extends Exception {

    private final int code;

    public ProfileException(int code, String reason) {
        super(reason);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
