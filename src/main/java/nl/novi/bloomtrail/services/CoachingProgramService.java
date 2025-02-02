package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.exceptions.EntityNotFoundException;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class CoachingProgramService {

    private final CoachingProgramRepository coachingProgramRepository;
    private final EntityValidationHelper validationHelper;
    private final StepRepository stepRepository;

    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, EntityValidationHelper validationHelper, StepRepository stepRepository) {
        this.coachingProgramRepository = coachingProgramRepository;
        this.validationHelper = validationHelper;
        this.stepRepository = stepRepository;
    }

    public List<CoachingProgram> findByCoachingProgramNameIgnoreCase(String coachingProgramName) {
        return validationHelper.validateCoachingProgramName(coachingProgramName);
    }

    public CoachingProgram findById(Long coachingProgramId) {
        return validationHelper.validateCoachingProgram(coachingProgramId);
    }


    public List<SimpleCoachingProgramDto> getCoachingProgramDetails() {
        return coachingProgramRepository.findAllCoachingProgramDetails();
    }

    public List<CoachingProgram> getCoachingProgramsByUser(String username) {
        return coachingProgramRepository.findByUserUsername(username);
    }

    public List<Step> getStepsByCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        List<Step> steps = stepRepository.findStepsByCoachingProgram(coachingProgramId);
        if (steps.isEmpty()) {
            throw new RecordNotFoundException("No steps found for CoachingProgram with ID: " + coachingProgramId);
        }
        return steps;
    }

    @Transactional
    public CoachingProgram getCoachingProgramWithSteps(Long programId) {
        return coachingProgramRepository.findByIdWithSteps(programId)
                .orElseThrow(() -> new RecordNotFoundException("Coaching program not found"));
    }

    public CoachingProgram saveCoachingProgram(CoachingProgramInputDto inputDto) {

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        CoachingProgram coachingProgram = CoachingProgramMapper.toCoachingProgramEntity(inputDto, client, coach);
        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram updateCoachingProgram(Long coachingProgramId, @Valid CoachingProgramInputDto inputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        coachingProgram.setCoachingProgramName(inputDto.getCoachingProgramName());
        coachingProgram.setGoal(inputDto.getGoal());
        coachingProgram.setStartDate(DateConverter.convertToLocalDate(inputDto.getStartDate()));
        coachingProgram.setEndDate(DateConverter.convertToLocalDate(inputDto.getEndDate()));
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

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


    public double calculateProgressPercentage(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.isEmpty()) {
            return 0.0;
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted)
                .count();

        double progress = (double) completedSteps / timeline.size() * 100;
        coachingProgram.setProgress(progress);
        coachingProgramRepository.save(coachingProgram);

        return progress;
    }

}
