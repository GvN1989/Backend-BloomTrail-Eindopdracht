package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Component;

@Component
public class EntityValidationHelper {

    private final AssignmentRepository assignmentRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final StepRepository stepRepository;

    public EntityValidationHelper(AssignmentRepository assignmentRepository, SessionRepository sessionRepository, UserRepository userRepository, CoachingProgramRepository coachingProgramRepository, StepRepository stepRepository) {
        this.assignmentRepository = assignmentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.stepRepository = stepRepository;
    }

    public Assignment validateAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment" ,assignmentId));
    }

    public Session validateSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session" , sessionId));
    }

    public User validateUser(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("Username" , username));
    }

    public CoachingProgram validateCoachingProgram (Long coachingProgramId) {
        return coachingProgramRepository.findById(coachingProgramId)
                .orElseThrow(() -> new EntityNotFoundException("CoachingProgram" , coachingProgramId));
    }

    public Step validateStep (Long StepId) {
        return stepRepository.findById(StepId)
                .orElseThrow(() -> new EntityNotFoundException("Step" , StepId));
    }

}
