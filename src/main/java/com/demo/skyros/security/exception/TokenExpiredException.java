package com.demo.skyros.security.exception;

public class TokenExpiredException extends InvalidTokenException {

    public TokenExpiredException(String type) {
        super(type + " token expired");
    }

    public TokenExpiredException() {
        super("token expired");
    }

}
