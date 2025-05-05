package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.SessionInsightRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SessionInsightService {
    private final SessionInsightRepository sessionInsightRepository;
    private final FileService fileService;
    private final ValidationHelper validationHelper;

    public SessionInsightService(SessionInsightRepository sessionInsightRepository, FileService fileService, ValidationHelper validationHelper) {
        this.sessionInsightRepository = sessionInsightRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
    }

    public void uploadClientReflectionFile(Long sessionId,MultipartFile file) {
        Session session = validationHelper.validateSession(sessionId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        SessionInsight sessionInsight = getOrCreateBySession(session, username);
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION, sessionInsight);
    }

    public void uploadCoachNotesFile(Long sessionId, MultipartFile file) {
        Session session = validationHelper.validateSession(sessionId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        SessionInsight sessionInsight = getOrCreateBySession(session, username);
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_COACH_NOTES, sessionInsight);
    }

    public SessionInsight getOrCreateBySession(Session session, String username) {
        return sessionInsightRepository.findBySession(session)
                .orElseGet(() -> {
                    SessionInsight insight = new SessionInsight();
                    insight.setSession(session);
                    insight.setAuthor(username);
                    return sessionInsightRepository.save(insight);
                });
    }
}
