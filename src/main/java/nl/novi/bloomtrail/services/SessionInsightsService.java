package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.SessionInsights;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.UploadContext;
import nl.novi.bloomtrail.repositories.SessionInsightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SessionInsightsService {

    private final SessionInsightsRepository sessionInsightsRepository;
    private final FileService fileService;

    public SessionInsightsService(SessionInsightsRepository sessionInsightsRepository, FileService fileService) {
        this.sessionInsightsRepository = sessionInsightsRepository;
        this.fileService = fileService;
    }

    public void uploadFileForSessionInsights(MultipartFile file, Long sessionInsightId) {
        SessionInsights sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsights entity with ID " + sessionInsightId + " not found."));

        fileService.saveUpload(file, UploadContext.SESSION_INSIGHTS, sessionInsight);
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getUploadsForSessionInsights(Long sessionInsightId) {
        SessionInsights sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsights entity with ID " + sessionInsightId + " not found."));
        return fileService.getUploadsForParentEntity(sessionInsight);
    }

}
