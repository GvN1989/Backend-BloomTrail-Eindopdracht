package nl.novi.bloomtrail.exceptions;

import nl.novi.bloomtrail.helper.ErrorResponseBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.ObjectError;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseBuilder.build(409, ex.getMessage())
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBuilder.build(400, ex.getMessage())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponseBuilder.build(404, ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        String globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        String fullMessage = Stream.of(fieldErrors, globalErrors)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(
                ErrorResponseBuilder.build(400, fullMessage)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(
                ErrorResponseBuilder.build(403, ex.getMessage()),
                headers,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseBuilder.build(409, "Database constraint violation occurred. Please check the data and try again.")
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadableJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponseBuilder.build(400, "Invalid request format: Required fields may be missing or malformed.")
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return ResponseEntity.badRequest().body(
                ErrorResponseBuilder.build(400, String.join("; ", errors))
        );
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                ErrorResponseBuilder.build(413, "File is too large. Maximum allowed size is 10MB.")
        );
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBuilder.build(400, "Invalid multipart request. Please check the file and try again.")
        );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(
                ErrorResponseBuilder.build(ex.getStatus().value(), ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponseBuilder.build(400, "Invalid argument provided.")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return ResponseEntity.status(500).body(
                ErrorResponseBuilder.build(500, "An unexpected error occurred." + ex.getClass().getSimpleName() + " - " + ex.getMessage())
        );
    }
}
