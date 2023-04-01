package com.demo.skyros.exception;

public class RoleNotFoundException extends ResourceNotFoundException {

    public RoleNotFoundException(Long id) {
        super("role not found with Id : " + id);
    }

    public RoleNotFoundException(String code) {
        super("role not found with code : " + code);
    }
}
