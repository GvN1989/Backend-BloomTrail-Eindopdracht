package nl.novi.bloomtrail.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {
    private final HttpStatus status;

    public ApplicationException(HttpStatus status,String message ) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
