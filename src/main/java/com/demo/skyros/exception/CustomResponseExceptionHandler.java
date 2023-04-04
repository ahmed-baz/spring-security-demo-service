package com.demo.skyros.exception;

import com.demo.skyros.security.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        return responseEntity;
    }

    @ExceptionHandler({UserNotFoundException.class, TokenNotFoundException.class, RoleNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.NOT_FOUND);
        return responseEntity;
    }

    @ExceptionHandler({TokenExpiredException.class, InvalidOtpException.class})
    public ResponseEntity<Object> handleTokenExpiredException(InvalidTokenException ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.UNAUTHORIZED);
        return responseEntity;
    }

    @ExceptionHandler(OtpRequiredException.class)
    public ResponseEntity<Object> handleOtpRequiredException(OtpRequiredException ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.PRECONDITION_REQUIRED, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.PRECONDITION_REQUIRED);
        return responseEntity;
    }

    @ExceptionHandler(ForceChangePasswordException.class)
    public ResponseEntity<Object> handleForceChangePasswordException(ForceChangePasswordException ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.UNAUTHORIZED);
        return responseEntity;
    }

    @ExceptionHandler(NoneUniqueResultException.class)
    public ResponseEntity<Object> handleNoneUniqueResultException(NoneUniqueResultException ex, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.CONFLICT, ex.getMessage(), request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.CONFLICT);
        return responseEntity;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        AppResponse appResponse = new AppResponse(new Date(), HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getBindingResult().getFieldError().getDefaultMessage());
        ResponseEntity responseEntity = new ResponseEntity(appResponse, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }
}
