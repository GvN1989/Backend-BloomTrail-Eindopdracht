package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.CoachingProgramPatchDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.helper.ValidationHelper;
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
    private final ValidationHelper validationHelper;
    private final StepRepository stepRepository;

    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, ValidationHelper validationHelper, StepRepository stepRepository) {
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
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        validationHelper.validateUser(username);
        return coachingProgramRepository.findByUserUsername(username);
    }

    @Transactional
    public CoachingProgram getCoachingProgramWithSteps(Long programId) {
        return coachingProgramRepository.findByIdWithSteps(programId)
                .orElseThrow(() -> new NotFoundException("Coaching program not found"));
    }

    public CoachingProgram saveCoachingProgram(CoachingProgramInputDto inputDto) {

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        CoachingProgram coachingProgram = CoachingProgramMapper.toCoachingProgramEntity(inputDto, client, coach);
        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram updateCoachingProgram(String username, Long coachingProgramId, @Valid CoachingProgramPatchDto patchInputDto) {
        User user = validationHelper.validateUser(username);
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        if (patchInputDto.getCoachUsername() != null) {
            User coach = validationHelper.validateUser(patchInputDto.getCoachUsername());
            coachingProgram.setCoach(coach);
        }

        CoachingProgramMapper.updateCoachingProgramFromPatchDto(coachingProgram, patchInputDto);

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
