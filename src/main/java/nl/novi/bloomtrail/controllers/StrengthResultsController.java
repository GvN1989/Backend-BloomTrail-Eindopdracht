package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.DownloadService;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/strength-results")
public class StrengthResultsController {

    private final StrengthResultsService strengthResultsService;

    private final EntityValidationHelper validationHelper;

    private final DownloadService downloadService;

    public StrengthResultsController(StrengthResultsService strengthResultsService, EntityValidationHelper validationHelper, DownloadService downloadService) {
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
    public ResponseEntity<byte[]> downloadAllStrengthResults(@PathVariable("id") Long strengthResultsId) throws IOException {
        byte[] zipData = strengthResultsService.downloadStrengthResults(strengthResultsId, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("strength_results_complete.zip")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
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

    @GetMapping("/coaching-program/{programId}")
    public ResponseEntity<List<StrengthResults>> getStrengthResultsByCoachingProgram(@PathVariable("programId") Long programId) {
        List<StrengthResults> results = strengthResultsService.getStrengthResultsByCoachingProgram(programId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrengthResults> getStrengthResults(@PathVariable("id") Long strengthResultsId) {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(strengthResultsId);
        return ResponseEntity.ok(strengthResults);
    }

}
