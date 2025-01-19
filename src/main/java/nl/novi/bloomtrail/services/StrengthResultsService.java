package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import nl.novi.bloomtrail.repositories.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import nl.novi.bloomtrail.enums.FileContext;

import java.util.List;

@Service
public class StrengthResultsService {

    private final StrengthResultsRepository strengthResultsRepository;
    private final FileService fileService;

    public StrengthResultsService(StrengthResultsRepository strengthResultsRepository, FileRepository fileRepository, FileService fileService) {
        this.strengthResultsRepository = strengthResultsRepository;
        this.fileService = fileService;
    }

    public void saveClientUpload(MultipartFile file, Long strengthResultsId) {
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new IllegalArgumentException("StrengthResults not found"));

        fileService.saveUpload(file, FileContext.STRENGTH_RESULTS, strengthResults);
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getUploadsForStrengthResults(Long strengthResultsId) {
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new IllegalArgumentException("StrengthResults not found"));
        return fileService.getUploadsForParentEntity(strengthResults);
    }
}

