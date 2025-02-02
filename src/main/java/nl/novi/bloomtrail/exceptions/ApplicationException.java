package nl.novi.bloomtrail.exceptions;

public class ApplicationException extends RuntimeException {
    private final int statusCode;

    public ApplicationException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public ApplicationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
