package com.example.practice.beerservicemvc.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleJpaViolation(TransactionSystemException exception) {
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();
        if (exception.getCause().getCause() instanceof ConstraintViolationException violationException) {
            List<Map<String, String>> errors = violationException.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                    return errorMap;
                }).collect(Collectors.toList());

            return responseEntity.body(errors);
        }
        return responseEntity.build();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleNotFoundException(MethodArgumentNotValidException exception) {
        List<Map<String, String>> errorList = exception.getBindingResult().getFieldErrors()
            .stream()
            .map(fieldError -> {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                return errorMap;
            }).toList();
        return ResponseEntity.badRequest().body(errorList);
    }
}
