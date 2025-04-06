package nl.novi.bloomtrail.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND,message);
    }
}

