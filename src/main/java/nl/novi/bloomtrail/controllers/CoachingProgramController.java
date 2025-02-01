package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.*;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.services.CoachingProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.novi.bloomtrail.helper.EntityValidationHelper;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/coaching-programs")
public class CoachingProgramController {

    private final CoachingProgramService coachingProgramService;
    private final EntityValidationHelper validationHelper;


    public CoachingProgramController(CoachingProgramService coachingProgramService, EntityValidationHelper validationHelper) {
        this.coachingProgramService = coachingProgramService;
        this.validationHelper = validationHelper;
    }

    @GetMapping("/details")
    public ResponseEntity<List<SimpleCoachingProgramDto>> getCoachingProgramDetails() {
        List<SimpleCoachingProgramDto> coachingPrograms = coachingProgramService.getCoachingProgramDetails();
        return ResponseEntity.ok(coachingPrograms);
    }

    @GetMapping("/{id}")
    public ResponseEntity <CoachingProgramDto> getCoachingProgramById (@PathVariable("id") Long coachingProgramId) {
        CoachingProgram coachingProgram = coachingProgramService.findById(coachingProgramId);
        CoachingProgramDto dto = CoachingProgramMapper.toCoachingProgramDto(coachingProgram);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{username}")
    public List<CoachingProgram> getCoachingProgramsByUser(@PathVariable String username) {
        return coachingProgramService.getCoachingProgramsByUser(username);
    }

    @GetMapping("/{id}/steps")
    public ResponseEntity<List<StepDto>> getStepsByCoachingProgram(@PathVariable("id") Long coachingProgramId) {
        List<Step> steps = coachingProgramService.getStepsByCoachingProgram(coachingProgramId);
        List<StepDto> response = steps.stream()
                .sorted(Comparator.comparingInt(Step::getSequence))
                .map(StepMapper::toStepDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity <Double> getProgressPercentage(@PathVariable("id") Long coachingProgramId) {
        double progress = coachingProgramService.calculateProgressPercentage(coachingProgramId);
        return ResponseEntity.ok(progress);
    }


    @PostMapping
        public ResponseEntity <CoachingProgramDto> addCoachingProgram (@Valid @RequestBody CoachingProgramInputDto inputDto) {
            CoachingProgram savedCoachingProgram = coachingProgramService.saveCoachingProgram(inputDto);
            CoachingProgramDto savedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(savedCoachingProgram);
            return ResponseEntity.created(URI.create("/coaching-programs/" + savedCoachingProgramDto.getCoachingProgramId())).body(savedCoachingProgramDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteCoachingProgram(@PathVariable("id") Long coachingProgramId) {
        coachingProgramService.deleteCoachingProgram(coachingProgramId);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}")
    public ResponseEntity <CoachingProgramDto> updateCoachingProgram (@Valid @PathVariable("id") Long coachingProgramId, @RequestBody CoachingProgramInputDto inputDto) {
        CoachingProgram updatedCoachingProgram = coachingProgramService.updateCoachingProgram(coachingProgramId, inputDto);
        CoachingProgramDto updatedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(updatedCoachingProgram);
        return ResponseEntity.ok().body(updatedCoachingProgramDto);

    }

    @PutMapping("/{id}/step}")
    public ResponseEntity <CoachingProgramDto> assignStepToCoachingProgram (@Valid @PathVariable("id") Long coachingProgramId, @RequestBody StepInputDto inputDto) {

        Step step = validationHelper.validateStep(inputDto.getStepId());
        CoachingProgram updatedCoachingProgram = coachingProgramService.assignStepToCoachingProgram(coachingProgramId, step);
        CoachingProgramDto updatedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(updatedCoachingProgram);
        return ResponseEntity.ok().body(updatedCoachingProgramDto);

    }

    @PutMapping("/{id}/strength-results")
    public ResponseEntity<CoachingProgramDto> assignStrengthResults(@Valid @PathVariable("id") Long coachingProgramId,@RequestBody List<Long> strengthResultsIds) {

        CoachingProgram updatedCoachingProgram = coachingProgramService.assignStrengthResultsToCoachingProgram(coachingProgramId, strengthResultsIds);
        CoachingProgramDto updatedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(updatedCoachingProgram);

        return ResponseEntity.ok(updatedCoachingProgramDto);
    }

}
