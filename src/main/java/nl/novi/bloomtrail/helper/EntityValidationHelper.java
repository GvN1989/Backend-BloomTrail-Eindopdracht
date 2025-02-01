package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.exceptions.UsernameNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final SessionInsightRepository sessionInsightRepository;

    public EntityValidationHelper(AssignmentRepository assignmentRepository, SessionRepository sessionRepository, UserRepository userRepository, CoachingProgramRepository coachingProgramRepository, StepRepository stepRepository, StrengthResultsRepository strengthResultsRepository, SessionInsightRepository sessionInsightRepository) {
        this.assignmentRepository = assignmentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.stepRepository = stepRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.sessionInsightRepository = sessionInsightRepository;
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
        if (coachingProgramId == null) {
            throw new IllegalArgumentException("Coaching program ID cannot be null.");
        }

        return coachingProgramRepository.findById(coachingProgramId)
                .orElseThrow(() -> new EntityNotFoundException("CoachingProgram", coachingProgramId));
    }

    public Step validateStep (Long StepId) {
        return stepRepository.findById(StepId)
                .orElseThrow(() -> new EntityNotFoundException("Step with ID" , StepId));
    }


    public List<Step> validateSteps (List<Long> stepIds) {
        if (stepIds == null || stepIds.isEmpty()) {
            return List.of();
        }
        return stepIds.stream()
                .map(this::validateStep)
                .collect(Collectors.toList());
    }

    public List<Session> validateSessions(List<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        List<Session> sessions = sessionRepository.findAllById(sessionIds);
        if (sessions.size() != sessionIds.size()) {
            throw new RecordNotFoundException("Some session IDs are invalid: " + sessionIds);
        }
        return sessions;
    }

    public List<Assignment> validateAssignments(List<Long> assignmentIds) {
        if (assignmentIds == null || assignmentIds.isEmpty()) {
            return List.of();
        }
        List<Assignment> assignments = assignmentRepository.findAllById(assignmentIds);
        if (assignments.size() != assignmentIds.size()) {
            throw new RecordNotFoundException("Some assignment IDs are invalid: " + assignmentIds);
        }

        return assignments;
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
        return sessionInsightRepository.findById(sessionInsightId)
                .orElseThrow(() -> new EntityNotFoundException("StrengthResults", sessionInsightId));

    }

    public List<SessionInsight> validateSessionInsights(List<Long> sessionInsightIds) {
        if (sessionInsightIds == null || sessionInsightIds.isEmpty()) {
            return List.of();
        }
        return sessionInsightIds.stream()
                .map(this::validateSessionInsight)
                .collect(Collectors.toList());
    }
    public void validateSessionDateAndTime(Step step, Session session) {
        boolean hasConflict = step.getSession().stream()
                .anyMatch(existingSession ->
                        existingSession.getSessionDate().equals(session.getSessionDate()) &&
                                existingSession.getSessionTime().equals(session.getSessionTime())
                );

        if (hasConflict) {
            throw new IllegalArgumentException("A session already exists for the same date and time: "
                    + session.getSessionDate() + " " + session.getSessionTime());
        }
    }



}
