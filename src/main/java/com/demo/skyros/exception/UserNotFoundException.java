package com.demo.skyros.exception;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(Long id) {
        super("user not found with Id : " + id);
    }

    public UserNotFoundException(String userName) {
        super("user not found with : " + userName);
    }

}
