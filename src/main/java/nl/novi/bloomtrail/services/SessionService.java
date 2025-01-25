package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final EntityValidationHelper validationHelper;

    public SessionService(SessionRepository sessionRepository, CoachingProgramRepository coachingProgramRepository, EntityValidationHelper validationHelper) {
        this.sessionRepository = sessionRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.validationHelper = validationHelper;
    }

    public List<Session> getSessionsForUser(String username) {
        User user = validationHelper.validateUser(username);

        List<CoachingProgram> programs = coachingProgramRepository.findByUsername(username);
        if (programs.isEmpty()) {
            throw new RecordNotFoundException("The user with username " + username + " does not have any coaching programs");
        }

        List<Step> steps = programs.stream()
                .flatMap(program -> program.getTimeline().stream())
                .toList();

        if (steps.isEmpty()) {
            throw new RecordNotFoundException("The user with username " + username + " does not have any steps");
        }

        return steps.stream()
                .flatMap(step -> step.getSession().stream())
                .collect(Collectors.toList());
    }
    public List<Session> getSessionsForStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);
        return step.getSession();
    }

    public Session addSessionToStep(SessionInputDto inputDto) {
        Step step = validationHelper.validateStep(inputDto.getStepId());

        List<SessionInsight> sessionInsights = validationHelper.validateSessionInsights(inputDto.getSessionInsightsId());
        List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentId());


        Session session = SessionMapper.toSessionEntity(inputDto, step, sessionInsights, assignments);

        boolean hasConflict = step.getSession().stream()
                .anyMatch(existingSession ->
                        existingSession.getSessionDate().equals(session.getSessionDate()) &&
                                existingSession.getSessionTime().equals(session.getSessionTime())
                );

        if (hasConflict) {
            throw new IllegalArgumentException("A session already exists for the same date and time: "
                    + session.getSessionDate() + " " + session.getSessionTime());
        }

        session.setStep(step);
        return sessionRepository.save(session);
    }

    public Session updateSession(Long sessionId, Session updatedSession) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session with ID " + sessionId + " not found"));

        session.setSessionDate(updatedSession.getSessionDate());
        session.setSessionTime(updatedSession.getSessionTime());
        session.setLocation(updatedSession.getLocation());
        session.setComment(updatedSession.getComment());

        return sessionRepository.save(session);
    }

    public void deleteSession (Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RecordNotFoundException("No session found with ID " + sessionId);
        }
        sessionRepository.deleteById(sessionId);
    }

}
