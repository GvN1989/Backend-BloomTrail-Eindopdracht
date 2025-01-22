package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    private final StepRepository stepRepository;

    private final UserRepository userRepository;

    private final CoachingProgramRepository coachingProgramRepository;

    public SessionService(SessionRepository sessionRepository, StepRepository stepRepository, UserRepository userRepository, CoachingProgramRepository coachingProgramRepository) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
        this.coachingProgramRepository = coachingProgramRepository;
    }

    public List<Session> getSessionsForUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RecordNotFoundException("User with username " + username + "not found"));

        List<CoachingProgram> programs = coachingProgramRepository.findByUsername(username);
        if (programs.isEmpty()) {
            throw new RecordNotFoundException("The user with username " + username + " does not have any coaching programs");
        }

        List<Step> steps = programs.stream()
                .flatMap(program -> program.getTimeline().stream())
                .collect(Collectors.toList());

        if (steps.isEmpty()) {
            throw new RecordNotFoundException("The user with username " + username + " does not have any steps");
        }

        return steps.stream()
                .flatMap(step -> step.getSession().stream())
                .collect(Collectors.toList());
    }
    public List<Session> getSessionsForStep(Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));
        return step.getSession();
    }

    public Session addSessionToStep(SessionInputDto inputDto) {
        Step step = stepRepository.findById(inputDto.getStepId())
                .orElseThrow(() -> new IllegalArgumentException("Step not found"));

        Session session = SessionMapper.toSessionEntity(inputDto, step);

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
