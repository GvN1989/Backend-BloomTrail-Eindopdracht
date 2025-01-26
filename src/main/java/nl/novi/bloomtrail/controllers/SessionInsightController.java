package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.SessionInsightDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.mappers.SessionInsightsMapper;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.services.SessionInsightService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sessionInsight")
public class SessionInsightController {

    private final SessionInsightService sessionInsightService;

    public SessionInsightController(SessionInsightService sessionInsightService) {
        this.sessionInsightService = sessionInsightService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SessionInsightDto>> getSessionInsightsBySession(
            @PathVariable Long sessionId,
            @RequestParam(required = false) FileContext context
    ) {
        List<SessionInsight> sessionInsights;

        if (context != null) {
            sessionInsights = sessionInsightService.getSessionInsightsByContext(sessionId, context);
        } else {
            sessionInsights = sessionInsightService.getSessionInsightsBySession(sessionId);
        }

        List<SessionInsightDto> sessionInsightDtos = sessionInsights.stream()
                .map(SessionInsightsMapper::toSessionInsightDto)
                .toList();

        return ResponseEntity.ok(sessionInsightDtos);
    }

    @PostMapping("/{id}/client-reflection")
    public ResponseEntity<String> uploadClientReflectionFile(
            @PathVariable("id") Long sessionInsightId,
            @RequestParam("file") MultipartFile file) {

        sessionInsightService.uploadClientReflectionFile(file, sessionInsightId);
        return ResponseEntity.ok("Client reflection file uploaded successfully.");
    }

    @PostMapping("/{id}/coach-notes")
    public ResponseEntity<String> uploadCoachNotesFile(
            @PathVariable("id") Long sessionInsightId,
            @RequestParam("file") MultipartFile file) {

        sessionInsightService.uploadCoachNotesFile(file, sessionInsightId);
        return ResponseEntity.ok("Coach notes file uploaded successfully.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSessionInsight(@PathVariable("id") Long sessionInsightId) {
        sessionInsightService.deleteSessionInsight(sessionInsightId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadSessionInsightFiles(
            @PathVariable("id") Long sessionInsightId,
            @RequestParam(required = false) FileContext context
    ) throws IOException {
        byte[] fileData = sessionInsightService.downloadSessionInsightFiles(sessionInsightId, context);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String filename = (context == null)
                ? "session_insight_files.zip"
                : "session_insight_" + context.toString().toLowerCase() + ".zip";

        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }

}
