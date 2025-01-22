package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.SessionInsightsRepository;
import nl.novi.bloomtrail.utils.FileStorageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SessionInsightService {

    private final SessionInsightsRepository sessionInsightsRepository;
    private final FileService fileService;

    public SessionInsightService(SessionInsightsRepository sessionInsightsRepository, FileService fileService) {
        this.sessionInsightsRepository = sessionInsightsRepository;
        this.fileService = fileService;
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getSessionInsightsFiles(Long sessionInsightId, FileContext context) {
        SessionInsight sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsights entity with ID " + sessionInsightId + " not found."));

        List<File> allFiles = fileService.getUploadsForParentEntity(sessionInsight);

        return allFiles.stream()
                .filter(file -> file.getContext() == context)
                .toList();
    }

    public void uploadClientReflectionFile(MultipartFile file,  Long sessionInsightId) {
        SessionInsight sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsight not found"));

        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION, sessionInsight);
    }

    public void uploadCoachNotesFile(MultipartFile file, Long sessionInsightId) {
        SessionInsight sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsight not found"));

        fileService.saveFile(file, FileContext.SESSION_INSIGHTS_COACH_NOTES, sessionInsight);
    }



}
