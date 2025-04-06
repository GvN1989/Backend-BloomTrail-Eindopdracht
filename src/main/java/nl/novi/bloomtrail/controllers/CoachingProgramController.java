package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.*;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.services.CoachingProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/coaching-programs")
public class CoachingProgramController {

    private final CoachingProgramService coachingProgramService;


    public CoachingProgramController(CoachingProgramService coachingProgramService) {
        this.coachingProgramService = coachingProgramService;
    }

    @GetMapping("/summary")
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

    @GetMapping("/name")
    public ResponseEntity<List<CoachingProgramDto>> getCoachingProgramByName(@RequestParam("name") String coachingProgramName) {
        List<CoachingProgram> coachingPrograms = coachingProgramService.findByCoachingProgramNameIgnoreCase(coachingProgramName);

        if (coachingPrograms.isEmpty()) {
            throw new NotFoundException("No coaching programs found with this name.");
        }

        List<CoachingProgramDto> dtos = coachingPrograms.stream()
                .map(CoachingProgramMapper::toCoachingProgramDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/user/{username}")
    public List<CoachingProgramDto> getCoachingProgramsByUser(@PathVariable String username) {
        return coachingProgramService.getCoachingProgramsByUser(username)
                .stream()
                .map(CoachingProgramMapper::toCoachingProgramDto)
                .toList();
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

    @PutMapping("/{username}/{id}")
    public ResponseEntity <CoachingProgramDto> updateCoachingProgram (
            @PathVariable Long id,
            @PathVariable String username,
            @Valid @RequestBody CoachingProgramPatchDto inputDto
    ) {
        CoachingProgram updatedCoachingProgram = coachingProgramService.updateCoachingProgram(username,id,inputDto);
        CoachingProgramDto updatedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(updatedCoachingProgram);
        return ResponseEntity.ok().body(updatedCoachingProgramDto);
    }

}
