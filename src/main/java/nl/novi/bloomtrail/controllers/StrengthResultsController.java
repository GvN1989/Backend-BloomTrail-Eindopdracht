package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.StrengthResultsDto;
import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.DownloadService;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.novi.bloomtrail.mappers.StrengthResultsMapper;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping
    public ResponseEntity<StrengthResultsDto> addStrengthResultsEntry(
            @RequestBody @Valid StrengthResultsInputDto inputDto
    ) {
        StrengthResults strengthResults = strengthResultsService.addStrengthResultsEntry(inputDto);
        StrengthResultsDto responseDto = StrengthResultsMapper.toStrengthResultDto(strengthResults);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrengthResultsDto> modifyStrengthResultsEntry(
            @PathVariable("id") Long resultsId,
            @RequestBody @Valid StrengthResultsInputDto inputDto
    ) {
        StrengthResults updatedStrengthResults = strengthResultsService.modifyStrengthResultsEntry(resultsId, inputDto);
        StrengthResultsDto responseDto = StrengthResultsMapper.toStrengthResultDto(updatedStrengthResults);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFileToStrengthResults(
            @PathVariable("id") Long resultsId,
            @RequestPart("file") MultipartFile file
    ) {
        File uploadedFile = strengthResultsService.uploadFileToStrengthResults(file, resultsId);
        return ResponseEntity.ok("File uploaded successfully: " + uploadedFile.getUrl());
    }

    @PostMapping("/report")
    public ResponseEntity<StrengthResultsDto> createStrengthResultsReport(
            @RequestParam("username") String username
    ) {
        StrengthResults strengthResults = strengthResultsService.createStrengthResultsReport(username);
        StrengthResultsDto responseDto = StrengthResultsMapper.toStrengthResultDto(strengthResults);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrengthResultsEntry(
            @PathVariable("id") Long resultsId
    ) {
        strengthResultsService.deleteStrengthResultsEntry(resultsId);
        return ResponseEntity.noContent().build();
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
                .filename("strength_results_" + strengthResultsId + "report.pdf")
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
