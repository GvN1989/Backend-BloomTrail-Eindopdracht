package nl.novi.bloomtrail.exceptions;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " with ID " + id + " not found. ");
    }

}
