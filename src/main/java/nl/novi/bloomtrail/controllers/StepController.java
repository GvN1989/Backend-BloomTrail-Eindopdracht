package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.services.StepService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/step")
public class StepController {

    private final StepService stepService;

    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    @GetMapping("/{id}")
    public ResponseEntity <StepDto> getStepById (@PathVariable("id") Long stepId) {
        Step step = stepService.findById(stepId);
        StepDto dto = StepMapper.toStepDto(step);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<StepDto> addStepToProgram(@Valid @RequestBody StepInputDto inputDto) {
        Step step = stepService.addStepToProgram(inputDto);
        StepDto response = StepMapper.toStepDto(step);
        return ResponseEntity.created(URI.create("/steps/" + step.getStepId())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteStep(@PathVariable("id") Long stepId) {
        stepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}")
    public ResponseEntity <StepDto> updateStep (@Valid @PathVariable("id") Long stepId, @RequestBody StepInputDto inputDto) {
        Step updatedStep = stepService.updateStep(stepId, inputDto);
        StepDto updatedStepDto = StepMapper.toStepDto(updatedStep);
        return ResponseEntity.ok().body(updatedStepDto);

    }

    @PutMapping("/{id}/completion")
    public ResponseEntity<StepDto> markStepCompletionStatus(
            @PathVariable("id") Long stepId,
            @RequestParam("isCompleted") boolean isCompleted) {

        Step updatedStep = stepService.markStepCompletionStatus(stepId, isCompleted);

        StepDto stepDto = StepMapper.toStepDto(updatedStep);

        return ResponseEntity.ok(stepDto);
    }

    @PutMapping("/{id}/assignments/{assignmentId}")
    public ResponseEntity<StepDto> assignAssignmentToStep(
            @PathVariable("id") Long stepId,
            @PathVariable("assignmentId") Long assignmentId) {

        Step updatedStep = stepService.assignAssignmentToStep(stepId, assignmentId);
        StepDto stepDto = StepMapper.toStepDto(updatedStep);

        return ResponseEntity.ok(stepDto);
    }

    @GetMapping("/{stepId}/download-zip")
    public ResponseEntity<byte[]> downloadFilesForStep(@PathVariable Long stepId) throws IOException {
        byte[] zipData = stepService.downloadFilesForStep(stepId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("step_files_" + stepId + ".zip")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipData);
    }

}
