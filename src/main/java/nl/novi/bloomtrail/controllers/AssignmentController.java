package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.AssignmentDto;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.mappers.AssignmentMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.services.AssignmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @Valid @ModelAttribute AssignmentInputDto inputDto,
            @RequestPart(value = "files", required = false) MultipartFile[]files
    ) {
        Assignment assignment = assignmentService.createAssignment(inputDto, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AssignmentMapper.toAssignmentDto(assignment));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentDto> updateAssignment(
            @PathVariable Long id,
            @Valid @ModelAttribute AssignmentInputDto inputDto,
            @RequestPart(value = "files", required = false) MultipartFile[]files
    ) {
        Assignment assignment = assignmentService.updateAssignment(id, inputDto, files);

        return ResponseEntity.ok(AssignmentMapper.toAssignmentDto(assignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable("id") Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download-zip")
    public ResponseEntity<byte[]> downloadSingleAssignmentFiles(@PathVariable ("id") Long assignmentId) throws IOException {
        byte[] zipData = assignmentService.downloadAssignmentFiles(assignmentId);
        if (zipData == null || zipData.length == 0) {
            return ResponseEntity.noContent().build();
        }

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
