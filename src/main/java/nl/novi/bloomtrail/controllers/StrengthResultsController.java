package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.DownloadService;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/strength-results")
public class StrengthResultsController {

    private final StrengthResultsService strengthResultsService;

    private final ValidationHelper validationHelper;

    private final DownloadService downloadService;

    public StrengthResultsController(StrengthResultsService strengthResultsService, ValidationHelper validationHelper, DownloadService downloadService) {
        this.strengthResultsService = strengthResultsService;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> downloadStrengthResultsReport(@PathVariable("id") Long strengthResultsId) throws IOException {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(strengthResultsId);

        if (strengthResults.getStrengthResultsFilePath() == null) {
            throw new IllegalArgumentException("No report available for StrengthResults ID: " + strengthResultsId +
            ". You can generate a new report using POST /strength-results/{userId}/generate-report.");
        }

        byte[] fileData = downloadService.downloadFile(strengthResults.getStrengthResultsFilePath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("strength_results_report.pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }

    @GetMapping("/{id}/uploads")
    public ResponseEntity<byte[]> downloadStrengthResultsUploads(@PathVariable("id") Long strengthResultsId) throws IOException {
        byte[] zipData = strengthResultsService.downloadStrengthResults(strengthResultsId, false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("strength_results_uploads.zip")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }

    @GetMapping("/{id}/all")
    public ResponseEntity<byte[]> downloadAllStrengthResults(@PathVariable("id") Long resultsId) throws IOException {
        byte[] zipData = strengthResultsService.downloadStrengthResults(resultsId, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("strength_results_complete.zip")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadSingleStrengthResults(@PathVariable("id") Long resultsId) throws IOException {
        byte[] fileData = strengthResultsService.downloadStrengthResults(resultsId, false );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("strength_report_" + resultsId + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }

    @GetMapping("/download-multiple")
    public ResponseEntity<byte[]> downloadMultipleStrengthResults(@RequestParam List<Long> ids) throws IOException {
        try {
            List<byte[]> filesData = ids.stream()
                    .map(id -> {
                        try {
                            return downloadService.downloadStrengthResults(id, true);
                        } catch (IOException e) {
                            throw new RuntimeException("Error downloading StrengthResults for ID: " + id, e);
                        }
                    })
                    .toList();

            byte[] zipData = downloadService.createZipFromFiles(filesData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("strength_reports.zip")
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to generate ZIP: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping
    public ResponseEntity<StrengthResults> addStrengthResults(@RequestBody StrengthResultsInputDto inputDto) {
        StrengthResults strengthResults = strengthResultsService.addStrengthResultsEntry(inputDto);
        return ResponseEntity.ok(strengthResults);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrengthResults> modifyStrengthResults(
            @PathVariable("id") Long strengthResultsId,
            @RequestBody StrengthResultsInputDto inputDto) {
        StrengthResults updatedStrengthResults = strengthResultsService.modifyStrengthResultsEntry(strengthResultsId, inputDto);
        return ResponseEntity.ok(updatedStrengthResults);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrengthResults(@PathVariable("id") Long strengthResultsId) {
        strengthResultsService.deleteStrengthResultsEntry(strengthResultsId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/generate-report")
    public ResponseEntity<StrengthResults> generateStrengthResultsReport(@PathVariable("userId") String userId) {
        StrengthResults strengthResults = strengthResultsService.createStrengthResultsReport(userId);
        return ResponseEntity.ok(strengthResults);
    }

    @GetMapping("/coaching-program/{id}")
    public ResponseEntity<List<StrengthResults>> getStrengthResultsByCoachingProgram(@PathVariable("id") Long coachingProgramId) {
        List<StrengthResults> results = strengthResultsService.getStrengthResultsByCoachingProgram(coachingProgramId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrengthResults> getStrengthResults(@PathVariable("id") Long strengthResultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(strengthResultsId);
        return ResponseEntity.ok(strengthResults);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<File> uploadFileToStrengthResults(
            @PathVariable("id") Long strengthResultsId,
            @RequestParam("file") MultipartFile file) {
        File uploadedFile = strengthResultsService.uploadFileToStrengthResults(file, strengthResultsId);
        return ResponseEntity.ok(uploadedFile);
    }
}
