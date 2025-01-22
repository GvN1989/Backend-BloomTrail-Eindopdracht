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
import java.util.stream.Collectors;


@Service
public class CoachingProgramService {

    private final CoachingProgramRepository coachingProgramRepository;

    private final StrengthResultsRepository strengthResultsRepository;

    private final EntityValidationHelper validationHelper;


    public CoachingProgramService(CoachingProgramRepository coachingProgramRepository, StrengthResultsRepository strengthResultsRepository, EntityValidationHelper validationHelper) {
        this.coachingProgramRepository = coachingProgramRepository;
        this.strengthResultsRepository = strengthResultsRepository;
        this.validationHelper = validationHelper;
    }

    public List<CoachingProgram> findByUser(String username) {
        List<CoachingProgram> programs = coachingProgramRepository.findByUsername(username);
        if (programs.isEmpty()) {
            throw new RecordNotFoundException("No CoachingPrograms found for user with username: " + username);
        }
        return programs;
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

    public CoachingProgram assignClientAndCoachToCoachingProgram(Long coachingProgramId, String clientUsername, String coachUsername) {

        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        User client = validationHelper.validateUser(clientUsername);
        User coach = validationHelper.validateUser(coachUsername);

        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

        return coachingProgramRepository.save(coachingProgram);
    }

    public CoachingProgram assignStepToCoachingProgram(Long coachingProgramId, Long stepId) {

        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
        Step step = validationHelper.validateStep(stepId);

        List<Step> timeline = coachingProgram.getTimeline();

        if (timeline.contains(step)) {
            throw new IllegalArgumentException("Step is already part of the timeline.");
        }

        boolean stepNameExists = timeline.stream()
                .anyMatch(existingStep -> existingStep.getStepName().equalsIgnoreCase(step.getStepName()));
        if (stepNameExists) {
            throw new IllegalArgumentException("A step with the name '" + step.getStepName() + "' already exists in the timeline.");
        }
        timeline.add(step);
        coachingProgram.setTimeline(timeline);
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

    public CoachingProgram assignCoachingResultsToCoachingProgram(Long coachingId, Long strengthResultsId) {
        CoachingProgram coachingProgram = findById(coachingId);
        StrengthResults strengthResults = strengthResultsRepository.findById(strengthResultsId)
                .orElseThrow(() -> new RecordNotFoundException("CoachingResults with ID " + strengthResultsId + " not found"));

        coachingProgram.setStrengthResults(strengthResults);

        return coachingProgramRepository.save(coachingProgram);
    }

    public void updateProgramEndDate(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

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

    public List<Session> getAllSessionsInCoachingProgram(Long coachingId) {
        CoachingProgram coachingProgram = findById(coachingId);

        return coachingProgram.getTimeline().stream()
                .flatMap(step -> step.getSessions().stream())
                .collect(Collectors.toList());
    }

    public List<CoachingProgram> getProgramsByCoach(String coachUsername) {
        User coach = validationHelper.validateUser(coachUsername);
        return coach.getCoachingPrograms();
    }

}
