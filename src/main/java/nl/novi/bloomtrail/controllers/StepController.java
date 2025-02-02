package nl.novi.bloomtrail.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.services.StepService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/steps/batch")
    public ResponseEntity<List<StepDto>> addStepsToProgram(@Valid @RequestBody Object input) {

        System.out.println("Received JSON: " + input); // ✅ Debugging step

        ObjectMapper objectMapper = new ObjectMapper();

        List<StepDto> response;

        if (input instanceof Map) {
            StepInputDto stepDto = objectMapper.convertValue(input, StepInputDto.class);
            response = stepService.addStepsToProgram(List.of(stepDto)).stream()
                    .map(StepMapper::toStepDto)
                    .toList();
        }
        else if (input instanceof List) {
            List<StepInputDto> inputDtos = objectMapper.convertValue(input, new TypeReference<List<StepInputDto>>() {});
            response = stepService.addStepsToProgram(inputDtos).stream()
                    .map(StepMapper::toStepDto)
                    .toList();
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input format.");
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
