package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.DownloadService;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
            throw new IllegalArgumentException("No report available for StrengthResults ID: " + strengthResultsId);
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

}
