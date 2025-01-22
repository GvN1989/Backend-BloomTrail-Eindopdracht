package nl.novi.bloomtrail.exceptions;

public class EntityNotFoundException extends ApplicationException{

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id);
    }

}
