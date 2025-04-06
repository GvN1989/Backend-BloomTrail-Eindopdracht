package nl.novi.bloomtrail.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException(String message) {
        super( HttpStatus.FORBIDDEN,message);
    }
}

