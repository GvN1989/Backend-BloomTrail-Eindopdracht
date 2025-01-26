package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.SessionDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.mappers.SessionMapper;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.services.SessionService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
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
    public ResponseEntity<SessionDto> addSessionToStep(@RequestBody @Valid SessionInputDto inputDto) {
        Session session = sessionService.addSessionToStep(inputDto);
        SessionDto sessionDto = SessionMapper.toSessionDto(session);
        return ResponseEntity.ok(sessionDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity <SessionDto> updateSession (@Valid @PathVariable("id") Long sessionId, @RequestBody SessionInputDto inputDto) {
        Session updatedSession = sessionService.updateSession(sessionId, inputDto);
        SessionDto updatedSessionDto = SessionMapper.toSessionDto(updatedSession);
        return ResponseEntity.ok().body(updatedSessionDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteSession(@PathVariable("id") Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/session-insight/{id}")
    public ResponseEntity<SessionDto> assignSessionInsightToSession(
            @PathVariable("id") Long sessionId,
            @PathVariable("id") Long sessionInsightId) {

        Session updatedSession = sessionService.assignSessionInsightToSession (sessionId, sessionInsightId);
        SessionDto sessionDto = SessionMapper.toSessionDto(updatedSession);

        return ResponseEntity.ok(sessionDto);
    }

    @PutMapping("/{sessionId}/assignments/{assignmentId}")
    public ResponseEntity<SessionDto> assignAssignmentToSession(
            @PathVariable Long sessionId,
            @PathVariable Long assignmentId) {

        Session updatedSession = sessionService.assignAssignmentToSession(sessionId, assignmentId);
        SessionDto sessionDto = SessionMapper.toSessionDto(updatedSession);

        return ResponseEntity.ok(sessionDto);
    }

    @GetMapping("/{sessionId}/download-zip")
    public ResponseEntity<byte[]> downloadFilesForSession(@PathVariable Long sessionId) throws IOException {
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
