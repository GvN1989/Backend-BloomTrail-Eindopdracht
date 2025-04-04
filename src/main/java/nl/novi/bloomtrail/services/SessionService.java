package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.helper.TimeConverter;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final ValidationHelper validationHelper;
    private final DownloadService downloadService;

    public SessionService(SessionRepository sessionRepository, CoachingProgramRepository coachingProgramRepository, ValidationHelper validationHelper, DownloadService downloadService) {
        this.sessionRepository = sessionRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public List<Session> getSessionsForUser(String username) {
        User user = validationHelper.validateUser(username);

        List<CoachingProgram> programs = coachingProgramRepository.findByUserUsername(username);
        if (programs.isEmpty()) {
            throw new NotFoundException("The user with username " + username + " does not have any coaching programs");
        }

        List<Step> steps = programs.stream()
                .flatMap(program -> program.getTimeline().stream())
                .toList();

        if (steps.isEmpty()) {
            throw new NotFoundException("The user with username " + username + " does not have any steps");
        }

        return steps.stream()
                .flatMap(step -> step.getSession().stream())
                .collect(Collectors.toList());
    }

    public Session createSessionAndAddToStep(SessionInputDto inputDto) {
        Step step = validationHelper.validateStep(inputDto.getStepId());

        List<SessionInsight> sessionInsights = inputDto.getSessionInsightsId() == null || inputDto.getSessionInsightsId().isEmpty()
                ? List.of()
                : validationHelper.validateSessionInsights(inputDto.getSessionInsightsId());

        List<Assignment> assignments = inputDto.getAssignmentId() == null || inputDto.getAssignmentId().isEmpty()
                ? List.of()
                : validationHelper.validateAssignments(inputDto.getAssignmentId());

        Session session = SessionMapper.toSessionEntity(inputDto, step, sessionInsights, assignments);

        validationHelper.validateSessionDateAndTime(step, session);

        session.setStep(step);
        return sessionRepository.save(session);
    }

    public Session updateSession(Long sessionId, SessionInputDto inputDto) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session with ID " + sessionId + " not found"));

        if (inputDto.getSessionDate() != null) {
            session.setSessionDate(DateConverter.convertToLocalDate(inputDto.getSessionDate()));
        }
        if (inputDto.getSessionTime() != null) {
            session.setSessionTime(TimeConverter.convertToLocalTime(inputDto.getSessionTime()));
        }
        if (inputDto.getLocation() != null) {
            session.setLocation(inputDto.getLocation());
        }
        if (inputDto.getComment() != null) {
            session.setComment(inputDto.getComment());
        }

        return sessionRepository.save(session);
    }

    public void deleteSession(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new NotFoundException("No session found with ID " + sessionId);
        }
        sessionRepository.deleteById(sessionId);
    }

    public byte[] downloadFilesForSession(Long sessionId) throws IOException {
        Session session = validationHelper.validateSession(sessionId);
        return downloadService.downloadFilesForEntity(session);
    }

}
