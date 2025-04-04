package nl.novi.bloomtrail.controllers;

import jakarta.validation.ConstraintViolationException;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
    public class ExceptionController {



        @ExceptionHandler(value = IndexOutOfBoundsException.class)
        public ResponseEntity<Object> exception (IndexOutOfBoundsException exception) {
            return new ResponseEntity<> ("This ID does not exist in the database", HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<String> handleEntityNotFoundException(NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<String> handleMappingException(ForbiddenException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<String> handleJsonMappingException(HttpMessageNotReadableException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request format: " + e.getMostSpecificCause().getMessage());
        }
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleGlobalException (Exception exception) {
            return new ResponseEntity<>("An unexptected error occured" + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


