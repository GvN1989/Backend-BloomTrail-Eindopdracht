package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    private final StepRepository stepRepository;

    public SessionService(SessionRepository sessionRepository, StepRepository stepRepository) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
    }

    public List<Session> getSessionsForStep(Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));
        return step.getSessions();
    }

    public Session addSessionToStep(Long stepId, Session session) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));

        session.setStep(step);
        return sessionRepository.save(session);
    }

    //delete session
}
