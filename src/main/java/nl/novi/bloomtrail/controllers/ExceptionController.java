package nl.novi.bloomtrail.controllers;

import jakarta.validation.ConstraintViolationException;
import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.MappingException;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
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

        @ExceptionHandler(value = RecordNotFoundException.class)
        public ResponseEntity <Object> exception(RecordNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(),HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(value = IndexOutOfBoundsException.class)
        public ResponseEntity<Object> exception (IndexOutOfBoundsException exception) {
            return new ResponseEntity<> ("Dit id staat niet in de database", HttpStatus.NOT_FOUND);
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

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(MappingException.class)
        public ResponseEntity<String> handleMappingException(MappingException ex) {
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


