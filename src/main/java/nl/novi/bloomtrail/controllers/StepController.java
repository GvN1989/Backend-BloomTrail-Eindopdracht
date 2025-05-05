package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.services.StepService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Comparator;
import java.util.List;


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
        return ResponseEntity.ok(StepMapper.toStepDto(step));
    }

    @GetMapping("/steps/{username}/{programId}")
    public ResponseEntity<List<StepDto>> getStepsForProgram(
            @PathVariable String username,
            @PathVariable Long programId
    ) {
        List<Step> steps = stepService.getStepsForUserAndProgram(username, programId);
        List<StepDto> response = steps.stream()
                .sorted(Comparator.comparingInt(Step::getSequence))
                .map(StepMapper::toStepDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{programId}/step")
    public ResponseEntity<List<StepDto>> addStepsToProgram(
            @PathVariable Long programId,
            @Valid @RequestBody StepInputDto inputDto
    ) {

        inputDto.setCoachingProgramId(programId);
        List<StepInputDto> inputDtos = List.of(inputDto);


        List<StepDto> response = stepService.addStepsToProgram(inputDtos).stream()
                .map(StepMapper::toStepDto)
                .toList();


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{programId}/steps-batch")
    public ResponseEntity<List<StepDto>> addStepsToProgramBatch(
            @PathVariable Long programId,
            @Valid @RequestBody List<StepInputDto> inputDtos) {

        inputDtos.forEach(dto -> dto.setCoachingProgramId(programId));

        List<StepDto> response = stepService.addStepsToProgram(inputDtos).stream()
                .map(StepMapper::toStepDto)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity <StepDto> updateStepDetails (@PathVariable("id") Long stepId, @RequestBody StepInputDto inputDto) {
        Step updatedStep = stepService.updateStepDetails(stepId, inputDto);
        StepDto updatedStepDto = StepMapper.toStepDto(updatedStep);
        return ResponseEntity.ok().body(updatedStepDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteStep(@PathVariable("id") Long stepId) {
        stepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download-zip")
    public ResponseEntity<byte[]> downloadFilesForStep(@PathVariable("id") Long stepId) throws IOException {
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
