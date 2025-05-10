package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.*;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.services.CoachingProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/coaching-programs")
public class CoachingProgramController {

    private final CoachingProgramService coachingProgramService;


    public CoachingProgramController(CoachingProgramService coachingProgramService) {
        this.coachingProgramService = coachingProgramService;
    }

    @GetMapping
    public ResponseEntity<List<SimpleCoachingProgramDto>> getCoachingProgramDetails() {
        List<SimpleCoachingProgramDto> coachingPrograms = coachingProgramService.getCoachingProgramDetails();
        return ResponseEntity.ok(coachingPrograms);
    }

    @GetMapping("/coaching-programs/clients")
    public ResponseEntity<List<SimpleCoachingProgramDto>> getCoachingProgramsForCoach() {
        String coachUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SimpleCoachingProgramDto> programs = coachingProgramService.getCoachingProgramSummariesForCoach(coachUsername);
        return ResponseEntity.ok(programs);
    }

    @GetMapping("/{id}")
    public ResponseEntity <CoachingProgramDto> getCoachingProgramById (@PathVariable("id") Long coachingProgramId) {
        CoachingProgramDto dto = coachingProgramService.findById(coachingProgramId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{username}")
    public List<CoachingProgramDto> getCoachingProgramsByUser(@PathVariable String username) {
        return coachingProgramService.getCoachingProgramsByUser(username);
    }

    @PostMapping
        public ResponseEntity <CoachingProgramDto> addCoachingProgram (@Valid @RequestBody CoachingProgramInputDto inputDto) {
            CoachingProgram savedCoachingProgram = coachingProgramService.saveCoachingProgram(inputDto);
            CoachingProgramDto savedCoachingProgramDto = CoachingProgramMapper.toCoachingProgramDto(savedCoachingProgram, 0, 0.0);
            return ResponseEntity.created(URI.create("/coaching-programs/" + savedCoachingProgramDto.getCoachingProgramId())).body(savedCoachingProgramDto);

    }

    @PutMapping("/{username}/{id}")
    public ResponseEntity<CoachingProgramDto> updateCoachingProgram(
            @PathVariable Long id,
            @PathVariable String username,
            @Valid @RequestBody CoachingProgramUpdateDto inputDto
    ) {
        CoachingProgram updated = coachingProgramService.updateCoachingProgram(username, id, inputDto);
        CoachingProgramDto dto = coachingProgramService.toDtoWithMetrics(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteCoachingProgram(@PathVariable("id") Long coachingProgramId) {
        coachingProgramService.deleteCoachingProgram(coachingProgramId);
        return ResponseEntity.noContent().build();
    }



}
