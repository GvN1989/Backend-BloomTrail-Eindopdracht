package nl.novi.bloomtrail.exceptions;

public class MappingException extends ApplicationException{
    public MappingException(String message) {
        super(message);
    }


    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
