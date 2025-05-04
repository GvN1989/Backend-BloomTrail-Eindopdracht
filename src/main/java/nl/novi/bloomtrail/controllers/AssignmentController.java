package nl.novi.bloomtrail.controllers;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentDto> createAssignment(
            @RequestParam("description") String description,
            @RequestParam("stepId") Long stepId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        AssignmentInputDto inputDto = new AssignmentInputDto();
        inputDto.setDescription(description);
        inputDto.setStepId(stepId);

        Assignment assignment = assignmentService.createAssignment(inputDto, file);
        AssignmentDto responseDto = AssignmentMapper.toAssignmentDto(assignment);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
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
        byte[] zipData = assignmentService.downloadAssignmentFiles(assignmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
               .filename("assignment_files_" + assignmentId + ".zip")
               .build());


        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }
}
