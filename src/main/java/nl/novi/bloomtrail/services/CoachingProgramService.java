package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.mappers.CoachingProgramMapper;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CoachingProgramService {

    private final CoachingProgramRepository coachingProgramRepository;
    private final StrengthResultsRepository strengthResultsRepository;
    private final EntityValidationHelper validationHelper;
    private final StepRepository stepRepository;

    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, StrengthResultsRepository strengthResultsRepository, EntityValidationHelper validationHelper, StepRepository stepRepository) {
        this.coachingProgramRepository = coachingProgramRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.validationHelper = validationHelper;
        this.stepRepository = stepRepository;
    }

    public List<CoachingProgram> findByUser(String username) {
        return validationHelper.validateCoachingProgramsByUser(username);
    }

    public CoachingProgram findById(Long coachingProgramId) {
        return validationHelper.validateCoachingProgram(coachingProgramId);
    }

    public CoachingProgram saveCoachingProgram(CoachingProgramInputDto inputDto) {

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        CoachingProgram coachingProgram = CoachingProgramMapper.toCoachingProgramEntity(inputDto, client, coach);
        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram updateCoachingProgram(Long coachingProgramId, CoachingProgramInputDto inputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        User client = validationHelper.validateUser(inputDto.getClientUsername());
        User coach = validationHelper.validateUser(inputDto.getCoachUsername());

        coachingProgram.setCoachingProgramName(inputDto.getCoachingProgramName());
        coachingProgram.setGoal(inputDto.getGoal());
        coachingProgram.setStartDate(inputDto.getStartDate());
        coachingProgram.setEndDate(inputDto.getEndDate());
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

        return coachingProgramRepository.save(coachingProgram);
    }

    public void deleteCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        coachingProgramRepository.delete(coachingProgram);
    }

    public CoachingProgram assignStepToCoachingProgram(Long coachingProgramId, Long stepId) {

        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        Step step = validationHelper.validateStep(stepId);

        validationHelper.validateStepAssignment(coachingProgram, step);

        coachingProgram.getTimeline().add(step);
        return coachingProgramRepository.save(coachingProgram);
    }

    public double calculateProgressPercentage(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.isEmpty()) {
            return 0.0;
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted)
                .count();

        return (double) completedSteps / timeline.size() * 100;
    }

    public CoachingProgram assignStrengthResultsToCoachingProgram(Long coachingProgramId, List<Long> strengthResultsIds) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        List<StrengthResults> strengthResultsList = strengthResultsRepository.findAllById(strengthResultsIds);

        if (strengthResultsList.isEmpty()) {
            throw new RecordNotFoundException("No StrengthResults found for the provided IDs: " + strengthResultsIds);
        }

        strengthResultsList.forEach(strengthResults -> strengthResults.setCoachingProgram(coachingProgram));
        strengthResultsRepository.saveAll(strengthResultsList);

        return coachingProgram;
    }

    public void updateProgramEndDate(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        List<Step> steps = coachingProgram.getTimeline();
        if (!steps.isEmpty()) {
            Date latestEndDate = steps.stream()
                    .map(Step::getStepEndDate)
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElse(coachingProgram.getEndDate());
            if (latestEndDate.after(coachingProgram.getEndDate())) {
                coachingProgram.setEndDate(latestEndDate);
                coachingProgramRepository.save(coachingProgram);
            }
        }
    }
    public List<Step> getStepsByCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        return stepRepository.findStepsByCoachingProgram(coachingProgramId);
    }

    public List<CoachingProgram> getProgramsByCoach(String coachUsername) {
        User coach = validationHelper.validateUser(coachUsername);
        return coach.getCoachingPrograms();
    }

}
