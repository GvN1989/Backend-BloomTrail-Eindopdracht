package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import org.springframework.stereotype.Component;

@Component
public class EntityValidationHelper {

    private final AssignmentRepository assignmentRepository;
    private final SessionRepository sessionRepository;

    public EntityValidationHelper(AssignmentRepository assignmentRepository, SessionRepository sessionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.sessionRepository = sessionRepository;
    }

    public Assignment validateAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment" ,assignmentId));
    }

    public Session validateSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session" , sessionId));
    }

}
