package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationHelper {

    private final AssignmentRepository assignmentRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final StepRepository stepRepository;
    private final SessionInsightRepository sessionInsightRepository;

    public ValidationHelper(AssignmentRepository assignmentRepository, SessionRepository sessionRepository, UserRepository userRepository, CoachingProgramRepository coachingProgramRepository, StepRepository stepRepository, SessionInsightRepository sessionInsightRepository) {
        this.assignmentRepository = assignmentRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.stepRepository = stepRepository;
        this.sessionInsightRepository = sessionInsightRepository;
    }

    public Assignment validateAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment" + assignmentId));
    }

    public Session validateSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session" + sessionId));
    }

    public User validateUser(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    public CoachingProgram validateCoachingProgram(Long coachingProgramId) {
        if (coachingProgramId == null) {
            throw new IllegalArgumentException("Coaching program ID cannot be null.");
        }

        return coachingProgramRepository.findByCoachingProgramId(coachingProgramId)
                .orElseThrow(() -> new NotFoundException("CoachingProgram not found with ID:" + coachingProgramId));
    }

    public List<CoachingProgram> validateCoachingProgramName(String coachingProgramName) {
        List<CoachingProgram> programs = coachingProgramRepository.findByCoachingProgramNameIgnoreCase(coachingProgramName);

        if (programs.isEmpty()) {
            throw new NotFoundException("No Coaching Program found with name: " + coachingProgramName);
        }

        return programs;
    }

    public Step validateStep(Long StepId) {
        return stepRepository.findById(StepId)
                .orElseThrow(() -> new NotFoundException("Step not found"));
    }


    public List<Session> validateSessions(List<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        List<Session> sessions = sessionRepository.findAllById(sessionIds);
        if (sessions.size() != sessionIds.size()) {
            throw new NotFoundException("Some session IDs are invalid: " + sessionIds);
        }
        return sessions;
    }

    public List<Assignment> validateAssignments(List<Long> assignmentIds) {
        if (assignmentIds == null || assignmentIds.isEmpty()) {
            return List.of();
        }
        List<Assignment> assignments = assignmentRepository.findAllById(assignmentIds);
        if (assignments.size() != assignmentIds.size()) {
            throw new NotFoundException("Some assignment IDs are invalid: " + assignmentIds);
        }

        return assignments;
    }

    public SessionInsight validateSessionInsight(Long sessionInsightId) {
        return sessionInsightRepository.findById(sessionInsightId)
                .orElseThrow(() -> new NotFoundException("StrengthResults" + sessionInsightId));

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
        boolean hasConflict = step.getSessions().stream()
                .anyMatch(existingSession ->
                        existingSession.getSessionDate().equals(session.getSessionDate()) &&
                                existingSession.getSessionTime().equals(session.getSessionTime())
                );

        if (hasConflict) {
            throw new IllegalArgumentException("A session already exists for the same date and time: "
                    + session.getSessionDate() + " " + session.getSessionTime());
        }
    }

    public void validateStepCreationInput(StepInputDto inputDto) {

        if (inputDto.getStepName() == null || inputDto.getStepName().isBlank()) {
            throw new BadRequestException("Step name is required.");
        }
        if (inputDto.getStepGoal() == null || inputDto.getStepGoal().isBlank()) {
            throw new BadRequestException("Step goal is required.");
        }
        if (inputDto.getCompleted() == null) {
            throw new BadRequestException("Field 'completed' must not be null.");
        }
        if (inputDto.getCoachingProgramId() == null) {
            throw new BadRequestException("Coaching program ID is required.");
        }
        if (inputDto.getStepStartDate() == null || inputDto.getStepEndDate() == null) {
            throw new BadRequestException("Step start date and end date cannot be null.");
        }
        if (inputDto.getStepEndDate().isBefore(inputDto.getStepStartDate())) {
            throw new BadRequestException("Step end date cannot be before start date.");
        }
    }



}
