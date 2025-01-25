package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.exceptions.UsernameNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityValidationHelper {

    private final AssignmentRepository assignmentRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final StepRepository stepRepository;
    private final StrengthResultsRepository strengthResultsRepository;
    private final SessionInsightsRepository sessionInsightsRepository;

    public EntityValidationHelper(AssignmentRepository assignmentRepository, SessionRepository sessionRepository, UserRepository userRepository, CoachingProgramRepository coachingProgramRepository, StepRepository stepRepository, StrengthResultsRepository strengthResultsRepository, SessionInsightsRepository sessionInsightsRepository) {
        this.assignmentRepository = assignmentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.stepRepository = stepRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.sessionInsightsRepository = sessionInsightsRepository;
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
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public CoachingProgram validateCoachingProgram (Long coachingProgramId) {
        return coachingProgramRepository.findById(coachingProgramId)
                .orElseThrow(() -> new EntityNotFoundException("CoachingProgram" , coachingProgramId));
    }

    public Step validateStep (Long StepId) {
        return stepRepository.findById(StepId)
                .orElseThrow(() -> new EntityNotFoundException("Step" , StepId));
    }

    public List<Session> validateSessions(List<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }
        return sessionIds.stream()
                .map(this::validateSession)
                .collect(Collectors.toList());
    }

    public List<Assignment> validateAssignments(List<Long> assignmentIds) {
        if (assignmentIds == null || assignmentIds.isEmpty()) {
            return List.of();
        }
        return assignmentIds.stream()
                .map(this::validateAssignment)
                .collect(Collectors.toList());
    }
    public void validateStepAssignment(CoachingProgram coachingProgram, Step step) {
        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.contains(step)) {
            throw new IllegalArgumentException("Step is already part of the timeline.");
        }

        boolean stepNameExists = timeline.stream()
                .anyMatch(existingStep -> existingStep.getStepName().equalsIgnoreCase(step.getStepName()));
        if (stepNameExists) {
            throw new IllegalArgumentException("A step with the name '" + step.getStepName() + "' already exists in the timeline.");
        }
    }

    public List<CoachingProgram> validateCoachingProgramsByUser(String username) {
        List<CoachingProgram> programs = coachingProgramRepository.findByUsername(username);
        if (programs.isEmpty()) {
            throw new RecordNotFoundException("No CoachingPrograms found for user with username: " + username);
        }
        return programs;
    }

    public StrengthResults validateStrengthResult(Long resultId) {
        return strengthResultsRepository.findById(resultId)
                .orElseThrow(() -> new EntityNotFoundException("StrengthResults", resultId));
    }

    public List<StrengthResults> validateStrengthResults(List<Long> resultsIds) {
        if (resultsIds == null || resultsIds.isEmpty()) {
            return List.of();
        }
        return resultsIds.stream()
                .map(this::validateStrengthResult)
                .collect(Collectors.toList());
    }

    public SessionInsight validateSessionInsight(Long sessionInsightId) {
        return sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new EntityNotFoundException("StrengthResults", sessionInsightId));

    }
}
