package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.AssignmentDto;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.mappers.AssignmentMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.services.AssignmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/step/{stepId}")
    public ResponseEntity<List<AssignmentDto>> getAssignmentsByStep(@PathVariable Long stepId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByStep(stepId);
        List<AssignmentDto> assignmentDtos = assignments.stream()
                .map(AssignmentMapper::toAssignmentDto)
                .toList();
        return ResponseEntity.ok(assignmentDtos);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AssignmentDto>> getAssignmentsBySession(@PathVariable Long sessionId) {
        List<Assignment> assignments = assignmentService.getAssignmentsBySession(sessionId);
        List<AssignmentDto> assignmentDtos = assignments.stream()
                .map(AssignmentMapper::toAssignmentDto)
                .toList();
        return ResponseEntity.ok(assignmentDtos);
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @RequestPart("data") AssignmentInputDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Assignment assignment = assignmentService.createAssignment(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDto> updateAssignment(
            @PathVariable("id") Long assignmentId,
            @Valid @RequestBody AssignmentInputDto inputDto) {

        Assignment updatedAssignment = assignmentService.updateAssignment(assignmentId, inputDto);
        AssignmentDto response = AssignmentMapper.toAssignmentDto(updatedAssignment);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFileForAssignment(
            @PathVariable("id") Long assignmentId,
            @RequestPart("file") MultipartFile file
    ) {
        assignmentService.uploadFileForAssignment(file, assignmentId);
        return ResponseEntity.ok("File uploaded successfully for assignment with ID: " + assignmentId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable("id") Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{assignmentId}/download")
    public ResponseEntity<byte[]> downloadSingleAssignmentFiles(@PathVariable Long assignmentId) throws IOException {
        byte[] fileData = assignmentService.downloadAssignmentFiles(assignmentId);

        List<File> files = assignmentService.getUploadsForAssignment(assignmentId);

        HttpHeaders headers = new HttpHeaders();

        if (files.size() == 1) {
            File singleFile = files.get(0);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(Paths.get(singleFile.getUrl()).getFileName().toString())
                    .build());
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("assignment_files_" + assignmentId + ".zip")
                    .build());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }
}
