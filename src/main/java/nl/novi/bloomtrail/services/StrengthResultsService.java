package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.mappers.StrengthResultsMapper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.ManagingStrengthsRepository;
import nl.novi.bloomtrail.repositories.StrengthResultsRepository;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StrengthResultsService {
    private final StrengthResultsRepository strengthResultsRepository;
    private final ManagingStrengthsRepository managingStrengthsRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final FileService fileService;
    private final EntityValidationHelper validationHelper;
    private final DownloadService downloadService;

    public StrengthResultsService(StrengthResultsRepository strengthResultsRepository, PdfGeneratorService pdfGeneratorService, FileService fileService, ManagingStrengthsRepository managingStrengthsRepository, EntityValidationHelper validationHelper, DownloadService downloadService) {
        this.strengthResultsRepository = strengthResultsRepository;
        this.managingStrengthsRepository = managingStrengthsRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public StrengthResults addStrengthResultsEntry(StrengthResultsInputDto inputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
        StrengthResults strengthResults = StrengthResultsMapper.toStrengthResultsEntity(inputDto, coachingProgram);
        return strengthResultsRepository.save(strengthResults);
    }

    public StrengthResults modifyStrengthResultsEntry(Long resultsId, StrengthResultsInputDto inputDto) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);

        strengthResults.setFilename(inputDto.getFilename());
        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());

        if (inputDto.getCoachingProgramId() != null) {
            CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
            strengthResults.setCoachingProgram(coachingProgram);
        }

        return strengthResultsRepository.save(strengthResults);
    }

    public File uploadFileToStrengthResults(MultipartFile file, Long resultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);
        return fileService.saveFile(file, FileContext.STRENGTH_RESULTS, strengthResults);
    }

    public StrengthResults createStrengthResultsReport(String userId) {
        List<ManagingStrength> topStrengths = managingStrengthsRepository.findTop15ByUsernameOrderByRank(userId);

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

    public List<File> getStrengthResultsUploads(Long resultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);
        return fileService.getUploadsForParentEntity(strengthResults);
    }

    public void deleteStrengthResultsEntry(Long resultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);
        fileService.deleteFilesForParentEntity(strengthResults);
        strengthResultsRepository.delete(strengthResults);
    }

    public List<StrengthResults> getStrengthResultsByCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        return strengthResultsRepository.findByCoachingProgram(coachingProgram);
    }

    public byte[] downloadStrengthResultFile(Long resultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);
        return downloadService.downloadFilesForParentEntity(strengthResults);
    }

}

