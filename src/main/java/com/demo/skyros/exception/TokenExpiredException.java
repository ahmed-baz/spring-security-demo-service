package com.demo.skyros.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String type) {
        super(type + " token expired");
    }

    public TokenExpiredException() {
        super("token expired");
    }

}
