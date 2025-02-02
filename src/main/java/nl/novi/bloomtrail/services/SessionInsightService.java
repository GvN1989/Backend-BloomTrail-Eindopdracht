package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.SessionInsightInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.SessionInsightRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SessionInsightService {
    private final SessionInsightRepository sessionInsightRepository;
    private final FileService fileService;
    private final EntityValidationHelper validationHelper;
    private final DownloadService downloadService;

    public SessionInsightService(SessionInsightRepository sessionInsightRepository, FileService fileService, EntityValidationHelper validationHelper, DownloadService downloadService) {
        this.sessionInsightRepository = sessionInsightRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public List<SessionInsight> getSessionInsightsByContext(Long sessionId, FileContext context) {
        Session session = validationHelper.validateSession(sessionId);

        return session.getSessionInsights().stream().filter(insight -> insight.getFileContext() == context).toList();
    }

    public List<SessionInsight> getSessionInsightsBySession(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);

        return session.getSessionInsights();
    }

    public SessionInsight createSessionInsight(SessionInsightInputDto inputDto) {
        if (!inputDto.isValid()) {
            throw new IllegalArgumentException("SessionInsight must be linked to a session.");
        }

        Session session = validationHelper.validateSession(inputDto.getSessionId());

        SessionInsight sessionInsight = new SessionInsight();
        sessionInsight.setAuthor(inputDto.getAuthor());
        sessionInsight.setDescription(inputDto.getDescription());
        sessionInsight.setFileContext(inputDto.getFileContext());
        sessionInsight.setSession(session);

        return sessionInsightRepository.save(sessionInsight);
    }

    public void uploadClientReflectionFile(MultipartFile file, Long sessionInsightId) {
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
        sessionInsightRepository.delete(sessionInsight);
    }

    public byte[] downloadSessionInsightFiles(Long sessionInsightId, FileContext context) throws IOException {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);
        return downloadService.downloadSessionInsightFiles(sessionInsightId, context);
    }


}
