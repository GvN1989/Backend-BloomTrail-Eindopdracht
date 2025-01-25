package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.MappingException;
import nl.novi.bloomtrail.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
    public class ExceptionController {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<String> handleValidationException(ValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(MappingException.class)
        public ResponseEntity<String> handleMappingException(MappingException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

    }


