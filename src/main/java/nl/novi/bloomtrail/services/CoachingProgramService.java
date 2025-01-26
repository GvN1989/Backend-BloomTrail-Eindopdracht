package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
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

    public List<SimpleCoachingProgramDto> getCoachingProgramDetails() {
        return coachingProgramRepository.findAllCoachingProgramDetails();
    }

    public CoachingProgram findById(Long coachingProgramId) {
        return validationHelper.validateCoachingProgram(coachingProgramId);
    }

    public List<CoachingProgram> getCoachingProgramsByCoach(String coachUsername) {
        return coachingProgramRepository.findByCoachUsername(coachUsername);
    }

    public List<CoachingProgram> getCoachingProgramsByClient(String clientUsername) {
        return coachingProgramRepository.findByClientUsername(clientUsername);
    }

    public List<Step> getStepsByCoachingProgram(Long coachingProgramId) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        List<Step> steps = stepRepository.findStepsByCoachingProgram(coachingProgramId);
        if (steps.isEmpty()) {
            throw new RecordNotFoundException("No steps found for CoachingProgram with ID: " + coachingProgramId);
        }
        return steps;
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

    public CoachingProgram assignStepToCoachingProgram(Long coachingProgramId, Step step) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        if (coachingProgram.getTimeline().contains(step)) {
            throw new IllegalArgumentException("Step is already part of the coaching program timeline.");
        }

        validateStepSequence(coachingProgramId, step);

        coachingProgram.getTimeline().add(step);
        step.setCoachingProgram(coachingProgram);
        stepRepository.save(step);

        updateProgramEndDate(coachingProgramId);

        return coachingProgram;
    }

    private void validateStepSequence(Long coachingProgramId, Step newStep) {
        List<Step> existingSteps = stepRepository.findStepsByCoachingProgram(coachingProgramId);

        for (Step step : existingSteps) {
            if (step.getSequence().equals(newStep.getSequence())) {
                throw new IllegalArgumentException("A step with sequence " + newStep.getSequence() + " already exists in this program.");
            }

            if (step.getStepStartDate().after(newStep.getStepStartDate()) && step.getSequence() < newStep.getSequence()) {
                throw new IllegalArgumentException("The step's start date conflicts with the provided sequence.");
            }
        }
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




}
