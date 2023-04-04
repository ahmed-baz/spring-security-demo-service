package com.demo.skyros.security.exception;

public class InvalidOtpException extends InvalidTokenException {

    public InvalidOtpException() {
        super("invalid OTP");
    }

}
