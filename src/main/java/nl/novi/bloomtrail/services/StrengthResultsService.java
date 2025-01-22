package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.ManagingStrength;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.repositories.ManagingStrengthsRepository;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StrengthResultsService {
    private final StrengthResultsRepository strengthResultsRepository;
    private final ManagingStrengthsRepository managingStrengthsRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final FileService fileService;

    public StrengthResultsService(StrengthResultsRepository strengthResultsRepository, PdfGeneratorService pdfGeneratorService, FileService fileService, ManagingStrengthsRepository managingStrengthsRepository) {
        this.strengthResultsRepository = strengthResultsRepository;
        this.managingStrengthsRepository = managingStrengthsRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.fileService = fileService;
    }

    public File saveStrengthResultsUpload(MultipartFile file, Long resultsId) {
        StrengthResults strengthResults = strengthResultsRepository.findById(resultsId)
                .orElseThrow(() -> new IllegalArgumentException("StrengthResults not found"));

        return fileService.saveFile(file, FileContext.STRENGTH_RESULTS, strengthResults);
    }

    public StrengthResults generateStrengthResultsReport(Long userId) {
        List<ManagingStrength> topStrengths = managingStrengthsRepository.findTop15ByUserIdOrderByRank(userId);

        if (topStrengths.isEmpty()) {
            throw new IllegalArgumentException("No strengths found for user with ID: " + userId);
        }

        byte[] pdfData = pdfGeneratorService.createPdf(topStrengths);
        String pdfFileName = "strength-results-" + userId + "-" + System.currentTimeMillis() + ".pdf";
        File savedFile = fileService.saveFile(pdfData, pdfFileName, FileContext.STRENGTH_RESULTS);
        String savedFileUrl= savedFile.getUrl();

        List<String> topStrengthNames = topStrengths.stream()
                .map(ManagingStrength::getStrengthEn)
                .collect(Collectors.toList());

        StrengthResults strengthResults = new StrengthResults();
        strengthResults.setStrengthResultsFilePath(savedFileUrl);
        strengthResults.setTopStrengthNames(topStrengthNames);

        return strengthResultsRepository.save(strengthResults);
    }

    public List<File> getStrengthResultsUploads(Long strengthResultsId) {
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new IllegalArgumentException("StrengthResults not found"));

        return fileService.getUploadsForParentEntity(strengthResults);
    }

    public void deleteResultsUpload(Long uploadId) {
        fileService.deleteUpload(uploadId);
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

}

