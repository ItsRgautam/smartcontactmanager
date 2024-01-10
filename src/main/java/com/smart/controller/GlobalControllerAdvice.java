package com.smart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smart.Exception.AuthorizationFailedException;

@RestControllerAdvice
public class GlobalControllerAdvice {
	
	 @ExceptionHandler(AuthorizationFailedException.class)
	    public ResponseEntity<String> generateBadRequestResponse() {
	        return new ResponseEntity<String>(" Bad Request as book not found", HttpStatus.FORBIDDEN);
	 
	        
	 }

}
