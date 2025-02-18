package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.SessionInsightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SessionInsightService {

    private final SessionInsightsRepository sessionInsightsRepository;
    private final FileService fileService;
    private final EntityValidationHelper validationHelper;
    private final DownloadService downloadService;

    public SessionInsightService(SessionInsightsRepository sessionInsightsRepository, FileService fileService, EntityValidationHelper validationHelper, DownloadService downloadService) {
        this.sessionInsightsRepository = sessionInsightsRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public List<SessionInsight> getSessionInsightsByContext(Long sessionId, FileContext context) {
        Session session = validationHelper.validateSession(sessionId);

        return session.getSessionInsights().stream()
                .filter(insight -> insight.getFileContext() == context)
                .toList();
    }

    public List<SessionInsight> getSessionInsightsBySession(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);

        return session.getSessionInsights();
    }

    public void uploadClientReflectionFile(MultipartFile file,  Long sessionInsightId) {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION, sessionInsight);
    }

    public void uploadCoachNotesFile(MultipartFile file, Long sessionInsightId) {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);
        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_COACH_NOTES, sessionInsight);
    }

    public void deleteSessionInsight(Long sessionInsightId) {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);
        fileService.deleteFilesForParentEntity(sessionInsight);
        sessionInsightsRepository.delete(sessionInsight);
    }

    public byte[] downloadSessionInsightFiles(Long sessionInsightId, FileContext context) throws IOException {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);
        return downloadService.downloadSessionInsightFiles(sessionInsightId, context);
    }



}
