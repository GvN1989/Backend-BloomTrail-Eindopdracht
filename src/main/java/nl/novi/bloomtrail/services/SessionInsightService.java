package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.FileRepository;
import nl.novi.bloomtrail.repositories.SessionInsightRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SessionInsightService {
    private final SessionInsightRepository sessionInsightRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final ValidationHelper validationHelper;
    private final AccessValidator accessValidator;

    public SessionInsightService(SessionInsightRepository sessionInsightRepository, FileRepository fileRepository, FileService fileService, ValidationHelper validationHelper, AccessValidator accessValidator) {
        this.sessionInsightRepository = sessionInsightRepository;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
        this.accessValidator = accessValidator;
    }

    public void uploadClientReflectionFile(Long sessionId,MultipartFile file) {
        Session session = validationHelper.validateSession(sessionId);

        accessValidator.validateClientOwnsSession(session);

        SessionInsight sessionInsight = getOrCreateBySession(session, accessValidator.getAuthenticatedUsername());
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION, sessionInsight);
    }

    public void uploadCoachNotesFile(Long sessionId, MultipartFile file) {
        Session session = validationHelper.validateSession(sessionId);

        accessValidator.validateCoachOwnsSession(session);

        SessionInsight sessionInsight = getOrCreateBySession(session, accessValidator.getAuthenticatedUsername());
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_COACH_NOTES, sessionInsight);
    }

    private SessionInsight getOrCreateBySession(Session session, String username) {
        return sessionInsightRepository.findBySession(session)
                .orElseGet(() -> {
                    SessionInsight insight = new SessionInsight();
                    insight.setSession(session);
                    insight.setAuthor(username);
                    return sessionInsightRepository.save(insight);
                });
    }

    public void deleteClientReflectionFiles(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);
        accessValidator.validateClientOwnsSession(session);

        SessionInsight insight = session.getSessionInsight();
        if (insight == null) {
            throw new NotFoundException("No session insight exists for this session.");
        }

        List<File> reflectionFiles = fileRepository.findBySessionInsight(insight).stream()
                .filter(file -> file.getContext() == FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION)
                .toList();

        reflectionFiles.forEach(fileService::deleteFile);
    }

    public void deleteCoachNotesFiles(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);
        accessValidator.validateCoachOwnsSession(session);

        SessionInsight insight = session.getSessionInsight();
        if (insight == null) {
            throw new NotFoundException("No session insight exists for this session.");
        }

        List<File> coachNoteFiles = fileRepository.findBySessionInsight(insight).stream()
                .filter(file -> file.getContext() == FileContext.SESSION_INSIGHTS_COACH_NOTES)
                .toList();

        coachNoteFiles.forEach(fileService::deleteFile);
    }



}
