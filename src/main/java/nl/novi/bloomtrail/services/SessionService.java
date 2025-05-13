package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final StepRepository stepRepository;
    private final CoachingProgramRepository coachingProgramRepository;
    private final ValidationHelper validationHelper;
    private final AccessValidator accessValidator;
    private final DownloadService downloadService;

    public SessionService(SessionRepository sessionRepository, StepRepository stepRepository, CoachingProgramRepository coachingProgramRepository, ValidationHelper validationHelper, AccessValidator accessValidator, DownloadService downloadService) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.validationHelper = validationHelper;
        this.accessValidator = accessValidator;
        this.downloadService = downloadService;
    }

    public List<Session> getSessionsForUser(String username) {
        User user = validationHelper.validateUser(username);


        List<CoachingProgram> programs = coachingProgramRepository.findByUserUsername(username);
        if (programs.isEmpty()) {
            throw new NotFoundException("The user with username " + username + " does not have any coaching programs");
        }

        accessValidator.validateSelfOrAffiliatedCoachOrAdminAccess(username, programs);

        List<Step> steps = programs.stream()
                .flatMap(program -> program.getTimeline().stream())
                .toList();

        if (steps.isEmpty()) {
            throw new NotFoundException("The user with username " + username + " does not have any steps");
        }

        return steps.stream()
                .flatMap(step -> step.getSessions().stream())
                .collect(Collectors.toList());
    }

    public Session createSessionAndAddToStep(SessionInputDto inputDto) {
        Step step = validationHelper.validateStep(inputDto.getStepId());
        validationHelper.validateStepBelongsToClient(step, inputDto.getClient());

        Session session = SessionMapper.toSessionEntity(inputDto, step);

        String resolvedCoach = accessValidator.resolveAndValidateCoachForClient(
                inputDto.getCoach(), inputDto.getClient());
        session.setCoach(resolvedCoach);

        validationHelper.validateSessionDateAndTime(step, session);

        session.setStep(step);
        return sessionRepository.save(session);
    }

    public Session updateSession(Long sessionId, SessionInputDto inputDto) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session with ID " + sessionId + " not found"));

        SessionMapper.updateSessionFromDto(session, inputDto);

        String resolvedCoach = accessValidator.resolveAndValidateCoachForClient(
                inputDto.getCoach(), inputDto.getClient());
        session.setCoach(resolvedCoach);

        if (inputDto.getStepId() != null && (session.getStep() == null || !session.getStep().getStepId().equals(inputDto.getStepId()))) {
            Step newStep = stepRepository.findById(inputDto.getStepId())
                    .orElseThrow(() -> new NotFoundException("Step with ID " + inputDto.getStepId() + " not found"));

            validationHelper.validateStepBelongsToClient(newStep, inputDto.getClient());

            session.setStep(newStep);
        }

        return sessionRepository.save(session);
    }

    public void deleteSession(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);
        accessValidator.validateCoachOrAdminAccess(session);

        sessionRepository.deleteById(sessionId);
    }


    public byte[] downloadFilesForSession(Long sessionId) throws IOException {
        Session session = validationHelper.validateSession(sessionId);

        accessValidator.validateCoachOrClientOrAdminAccess(session);

        return downloadService.downloadFilesForEntity(session);
    }

}
