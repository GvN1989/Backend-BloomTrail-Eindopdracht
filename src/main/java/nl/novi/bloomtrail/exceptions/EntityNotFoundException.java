package nl.novi.bloomtrail.exceptions;

public class EntityNotFoundException extends ApplicationException{

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " with identifier " + id + " was not found. ");
    }

}
