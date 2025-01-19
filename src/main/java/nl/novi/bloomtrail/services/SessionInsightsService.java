package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.SessionInsights;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.SessionInsightsRepository;
import nl.novi.bloomtrail.utils.FileStorageUtil;
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

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getSessionInsightsFiles(Long sessionInsightId, FileContext context) {
        SessionInsights sessionInsight = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsights entity with ID " + sessionInsightId + " not found."));

        List<File> allFiles = fileService.getUploadsForParentEntity(sessionInsight);

        return allFiles.stream()
                .filter(file -> file.getContext() == context)
                .toList();
    }

    public void uploadClientReflectionFile(MultipartFile multipartFile,  Long sessionInsightId) {
        SessionInsights sessionInsights = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsight not found"));

        File file = new File();
        file.setContext(FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION);
        file.setUrl(FileStorageUtil.saveFile(multipartFile));
        file.setSessionInsights(sessionInsights);

        fileService.processUpload(file);
    }

    public void uploadCoachNotesFile(MultipartFile multipartFile, Long sessionInsightId) {
        SessionInsights sessionInsights = sessionInsightsRepository.findById(sessionInsightId)
                .orElseThrow(() -> new IllegalArgumentException("SessionInsight not found"));

        File file = new File();
        file.setContext(FileContext.SESSION_INSIGHTS_COACH_NOTES);
        file.setUrl(FileStorageUtil.saveFile(multipartFile));
        file.setSessionInsights(sessionInsights);

        fileService.processUpload(file);
    }



}
