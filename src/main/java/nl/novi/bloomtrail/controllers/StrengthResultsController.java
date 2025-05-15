package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.StrengthResultsDto;
import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.mappers.StrengthResultsMapper;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strength-results")
public class StrengthResultsController {

    private final StrengthResultsService strengthResultsService;
    private final AccessValidator accessValidator;

    public StrengthResultsController(StrengthResultsService strengthResultsService, AccessValidator accessValidator) {
        this.strengthResultsService = strengthResultsService;
        this.accessValidator = accessValidator;
    }

    @GetMapping("/me")
    public ResponseEntity<StrengthResultsDto> getOwnStrengthResults() {
        String username = accessValidator.getAuthenticatedUser();
        StrengthResults result = strengthResultsService.getStrengthResultsByUsername(username);
        StrengthResultsDto dto = StrengthResultsMapper.toStrengthResultDto(result);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<StrengthResultsDto> getStrengthResultsByUsername(@PathVariable String username) {
        StrengthResults result = strengthResultsService.getStrengthResultsByUsername(username);
        StrengthResultsDto dto = StrengthResultsMapper.toStrengthResultDto(result);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<StrengthResultsDto> createStrengthResults(@Valid @RequestBody StrengthResultsInputDto inputDto) {
        String currentUsername = accessValidator.getAuthenticatedUser();
        StrengthResults created = strengthResultsService.createStrengthResults(inputDto, currentUsername);
        return ResponseEntity.ok(StrengthResultsMapper.toStrengthResultDto(created));
    }

    @PutMapping
    public ResponseEntity<StrengthResultsDto> modifyStrengthResults(@RequestBody StrengthResultsInputDto inputDto) {
        String username = accessValidator.getAuthenticatedUser();
        StrengthResults updated = strengthResultsService.modifyStrengthResults(inputDto, username);
        StrengthResultsDto dto = StrengthResultsMapper.toStrengthResultDto(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Void> deleteStrengthResultsForUser(@PathVariable String username) {
        strengthResultsService.deleteStrengthResultsByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
