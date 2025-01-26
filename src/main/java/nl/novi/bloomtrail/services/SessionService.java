package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.SessionInsightsRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final EntityValidationHelper validationHelper;
    private final SessionInsightsRepository sessionInsightsRepository;
    private final AssignmentRepository assignmentRepository;

    private final DownloadService downloadService;

    public SessionService(SessionRepository sessionRepository, CoachingProgramRepository coachingProgramRepository, EntityValidationHelper validationHelper, SessionInsightsRepository sessionInsightsRepository, AssignmentRepository assignmentRepository, DownloadService downloadService) {
        this.sessionRepository = sessionRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.validationHelper = validationHelper;
        this.sessionInsightsRepository = sessionInsightsRepository;
        this.assignmentRepository = assignmentRepository;
        this.downloadService = downloadService;
    }

    public List<Session> getSessionsForUser(String username) {
        User user = validationHelper.validateUser(username);

        List<CoachingProgram> programs = coachingProgramRepository.findByUserUsername(username);
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

    public Session addSessionToStep(SessionInputDto inputDto) {
        Step step = validationHelper.validateStep(inputDto.getStepId());

        List<SessionInsight> sessionInsights = validationHelper.validateSessionInsights(inputDto.getSessionInsightsId());
        List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentId());


        Session session = SessionMapper.toSessionEntity(inputDto, step, sessionInsights, assignments);

        validationHelper.validateSessionDateAndTime(step, session);

        session.setStep(step);
        return sessionRepository.save(session);
    }

    public Session updateSession(Long sessionId, SessionInputDto inputDto) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session with ID " + sessionId + " not found"));

        if (inputDto.getSessionDate() != null) {
            session.setSessionDate(inputDto.getSessionDate());
        }
        if (inputDto.getSessionTime() != null) {
            session.setSessionTime(inputDto.getSessionTime());
        }
        if (inputDto.getLocation() != null) {
            session.setLocation(inputDto.getLocation());
        }
        if (inputDto.getComment() != null) {
            session.setComment(inputDto.getComment());
        }

        return sessionRepository.save(session);
    }

    public void deleteSession (Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RecordNotFoundException("No session found with ID " + sessionId);
        }
        sessionRepository.deleteById(sessionId);
    }

    public Session assignSessionInsightToSession(Long sessionId, Long sessionInsightId) {
        Session session = validationHelper.validateSession(sessionId);
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);

        if (session.getSessionInsights().contains(sessionInsight)) {
            throw new IllegalArgumentException("Session Insight is already associated with the session" + sessionId);
        }

        sessionInsight.setSession(session);
        session.getSessionInsights().add(sessionInsight);

        sessionRepository.save(session);
        sessionInsightsRepository.save(sessionInsight);

        return session;
    }

    public Session assignAssignmentToSession(Long sessionId, Long assignmentId) {
        Session session = validationHelper.validateSession(sessionId);
        Assignment assignment = validationHelper.validateAssignment(assignmentId);

        if (assignment.getSession() != null) {
            throw new IllegalArgumentException("Assignment is already associated with session" + sessionId );
        }

        assignment.setSession(session);
        session.getAssignment().add(assignment);

        assignmentRepository.save(assignment);
        return sessionRepository.save(session);
    }

    public byte[] downloadFilesForSession(Long sessionId) throws IOException {
        Session session = validationHelper.validateSession(sessionId);
        return downloadService.downloadFilesForEntity(session);
    }

}
