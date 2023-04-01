package com.demo.skyros.exception;

public class TokenNotFoundException extends ResourceNotFoundException {

    public TokenNotFoundException() {
        super("invalid token");
    }

}
