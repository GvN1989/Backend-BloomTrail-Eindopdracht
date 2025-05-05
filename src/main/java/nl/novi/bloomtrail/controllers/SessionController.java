package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.SessionDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.services.SessionInsightService;
import nl.novi.bloomtrail.services.SessionService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    private final SessionInsightService sessionInsightService;

    public SessionController(SessionService sessionService, SessionInsightService sessionInsightService) {
        this.sessionService = sessionService;
        this.sessionInsightService = sessionInsightService;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<SessionDto>> getSessionsForUser(@PathVariable String username) {
        List<Session> sessions = sessionService.getSessionsForUser(username);

        List<SessionDto> sessionDtos = sessions.stream()
                .map(SessionMapper::toSessionDto)
                .toList();

        return ResponseEntity.ok(sessionDtos);
    }
    @PostMapping
    public ResponseEntity<SessionDto> createSessionAndAddToStep(@RequestBody @Valid SessionInputDto inputDto) {
        Session session = sessionService.createSessionAndAddToStep(inputDto);
        SessionDto sessionDto = SessionMapper.toSessionDto(session);
        return ResponseEntity.ok(sessionDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity <SessionDto> updateSession (@Valid @PathVariable("id") Long sessionId, @RequestBody SessionInputDto inputDto) {
        Session updatedSession = sessionService.updateSession(sessionId, inputDto);
        SessionDto updatedSessionDto = SessionMapper.toSessionDto(updatedSession);
        return ResponseEntity.ok().body(updatedSessionDto);

    }

    @PostMapping("/{id}/client-reflection")
    public ResponseEntity<String> uploadClientReflectionFile(
            @PathVariable ("id") Long sessionId,
            @RequestParam("file") MultipartFile file) {

        sessionInsightService.uploadClientReflectionFile(sessionId,file);
        return ResponseEntity.ok("Client reflection file uploaded successfully.");
    }

    @PostMapping("/{id}/coach-notes")
    public ResponseEntity<String> uploadCoachNotesFile(
            @PathVariable ("id") Long sessionId,
            @RequestParam("file") MultipartFile file) {

        sessionInsightService.uploadCoachNotesFile(sessionId,file);
        return ResponseEntity.ok("Coach notes file uploaded successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteSession(@PathVariable("id") Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download-zip")
    public ResponseEntity<byte[]> downloadFilesForSession(@PathVariable ("id") Long sessionId) throws IOException {
        byte[] zipData = sessionService.downloadFilesForSession(sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("session_files_" + sessionId + ".zip")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }

}
