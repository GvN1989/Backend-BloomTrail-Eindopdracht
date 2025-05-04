package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.StrengthResultsDto;
import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.mappers.StrengthResultsMapper;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.services.StrengthResultsService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strength-results")
public class StrengthResultsController {

    private final StrengthResultsService strengthResultsService;

    public StrengthResultsController(StrengthResultsService strengthResultsService) {
        this.strengthResultsService = strengthResultsService;
    }

    @GetMapping("/me")
    public ResponseEntity<StrengthResultsDto> getOwnStrengthResults() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        StrengthResults result = strengthResultsService.getStrengthResultsByUsername(username);
        StrengthResultsDto dto = StrengthResultsMapper.toStrengthResultDto(result);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<StrengthResultsDto> getStrengthResultsByUsername(@PathVariable String username) {
        StrengthResults result = strengthResultsService.getStrengthResultsByUsername(username);
        return ResponseEntity.ok(StrengthResultsMapper.toStrengthResultDto(result));
    }

    @PostMapping
    public ResponseEntity<StrengthResults> createStrengthResults(@RequestBody StrengthResultsInputDto inputDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        StrengthResults strengthResults = strengthResultsService.createStrengthResults(inputDto, username);
        return ResponseEntity.ok(strengthResults);
    }

    @PostMapping("/user/{username}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<StrengthResults> createStrengthResultsForUser(
            @PathVariable String username,
            @RequestBody StrengthResultsInputDto inputDto) {

        StrengthResults result = strengthResultsService.createStrengthResults(inputDto, username);
        return ResponseEntity.ok(result);
    }
    @PutMapping
    public ResponseEntity<StrengthResults> modifyStrengthResults(@RequestBody StrengthResultsInputDto inputDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        StrengthResults updated = strengthResultsService.modifyStrengthResults(inputDto, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStrengthResultsForUser(@PathVariable String username) {
        strengthResultsService.deleteStrengthResultsByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
