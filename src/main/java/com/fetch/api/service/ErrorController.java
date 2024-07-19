package com.fetch.api.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fetch.api.model.CustomError;

@ControllerAdvice
@RestController
public class ErrorController extends ResponseEntityExceptionHandler {
	
	/**
	 * handleCustomException(): This handler is used to set the error response from 
	 * 							the CustomError object and sends it as a response.
	 */
	@ExceptionHandler(CustomError.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomError ex) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("code", ex.getCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
	
}

