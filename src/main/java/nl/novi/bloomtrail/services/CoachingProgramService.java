package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramDto;
import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.CoachingProgramUpdateDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class CoachingProgramService {

    private final CoachingProgramRepository coachingProgramRepository;

    private final StepRepository stepRepository;

    private final ValidationHelper validationHelper;

    private final AccessValidator accessValidator;

    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, StepRepository stepRepository, ValidationHelper validationHelper, AccessValidator accessValidator) {
        this.coachingProgramRepository = coachingProgramRepository;
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
        this.accessValidator = accessValidator;
    }

    public CoachingProgramDto findById(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        return toDtoWithMetrics(coachingProgram);
    }


    public List<SimpleCoachingProgramDto> getCoachingProgramDetails() {
        List<CoachingProgram> programs = coachingProgramRepository.findAll();

        return programs.stream()
                .map(this::toSimpleDtoWithMetrics)
                .toList();
    }

    public List<SimpleCoachingProgramDto> getCoachingProgramSummariesForCoach(String coachUsername) {
        List<CoachingProgram> programs = coachingProgramRepository.findAllByCoach_Username(coachUsername);

        return programs.stream()
                .map(this::toSimpleDtoWithMetrics)
                .toList();
    }


    public List<CoachingProgramDto> getCoachingProgramsByUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        validationHelper.validateUser(username);

        List<CoachingProgram> programs = coachingProgramRepository.findByUserUsername(username);

        return programs.stream()
                .map(this::toDtoWithMetrics)
                .toList();
    }

    public CoachingProgram saveCoachingProgram(CoachingProgramInputDto inputDto) {

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        CoachingProgram coachingProgram = CoachingProgramMapper.toCoachingProgramEntity(inputDto, client, coach);
        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram updateCoachingProgram(String username, Long coachingProgramId, @Valid CoachingProgramUpdateDto updateInputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        accessValidator.validateCoachOwnsProgramOrIsAdmin(coachingProgram);

        if (updateInputDto.getCoachUsername() != null) {
            User coach = validationHelper.validateUser(updateInputDto.getCoachUsername());
            coachingProgram.setCoach(coach);
        }

        CoachingProgramMapper.updateCoachingProgramDto(coachingProgram, updateInputDto);

        return coachingProgramRepository.save(coachingProgram);
    }

    public void deleteCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        coachingProgramRepository.delete(coachingProgram);
    }

    public void updateProgramEndDate(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        List<Step> steps = coachingProgram.getTimeline();
        if (steps == null || steps.isEmpty()) {
            return;
        }

        LocalDate latestEndDate = steps.stream()
                .map(Step::getStepEndDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        if (latestEndDate != null) {
            coachingProgram.setEndDate(latestEndDate);
            coachingProgramRepository.save(coachingProgram);
        }
    }


    public double calculateProgressPercentage(CoachingProgram coachingProgram) {
        accessValidator.validateAffiliatedUserOrAdmin(coachingProgram);

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.isEmpty()) {
            return 0.0;
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted)
                .count();

        return (double) completedSteps / timeline.size() * 100;
    }

    public CoachingProgramDto toDtoWithMetrics(CoachingProgram program) {
        int stepCount = stepRepository.countByCoachingProgram_CoachingProgramId(program.getCoachingProgramId());
        double progress = calculateProgressPercentage(program);
        return CoachingProgramMapper.toCoachingProgramDto(program, stepCount, progress);
    }

    public SimpleCoachingProgramDto toSimpleDtoWithMetrics(CoachingProgram program) {
        int stepCount = stepRepository.countByCoachingProgram_CoachingProgramId(program.getCoachingProgramId());
        double progress = calculateProgressPercentage(program);
        return CoachingProgramMapper.toSimpleDto(program, stepCount, progress);
    }


}
