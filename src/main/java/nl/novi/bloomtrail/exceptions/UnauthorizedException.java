package nl.novi.bloomtrail.exceptions;

public class UnauthorizedException extends ApplicationException{

    public UnauthorizedException(String message) {
        super(message, 401);
    }

}
