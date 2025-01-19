package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.ManagingStrength;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.repositories.ManagingStrengthsRepository;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ManagingStrengthService {
    private final ManagingStrengthsRepository managingStrengthsRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final StrengthResultsRepository strengthResultsRepository;

    public ManagingStrengthService(ManagingStrengthsRepository managingStrengthsRepository, PdfGeneratorService pdfGeneratorService, StrengthResultsRepository strengthResultsRepository) {
        this.managingStrengthsRepository = managingStrengthsRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.strengthResultsRepository = strengthResultsRepository;
    }

    public void processAndSaveStrengthResults(List<Integer> strengthIds, Long strengthResultsId) {
        List<ManagingStrength> strengths = managingStrengthsRepository.getManagingStrengths(strengthIds);

        byte[] pdfData = pdfGeneratorService.createPdf(strengths);

        String pdfFilePath = savePdfToLocalFileSystem(pdfData, "strength-results-" + strengthResultsId + ".pdf");

        StrengthResults results = new StrengthResults();
        results.setStrengthResultsId(strengthResultsId);
        results.setManagingStrengthPdf(pdfFilePath);
        strengthResultsRepository.save(results);
    }

    private String savePdfToLocalFileSystem(byte[] pdfData, String fileName) {
        try {
            Path uploadDir = Paths.get("src/main/resources/uploads/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, pdfData);
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PDF", e);
        }
    }
}
