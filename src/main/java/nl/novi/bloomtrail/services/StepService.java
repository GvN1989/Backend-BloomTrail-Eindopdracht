package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StepService {

    private final StepRepository stepRepository;
    private final EntityValidationHelper validationHelper;
    private final CoachingProgramService coachingProgramService;
    private final DownloadService downloadService;
    private final CoachingProgramRepository coachingProgramRepository;

    public StepService( StepRepository stepRepository, EntityValidationHelper validationHelper, CoachingProgramService coachingProgramService, DownloadService downloadService, CoachingProgramRepository coachingProgramRepository) {
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
        this.coachingProgramService = coachingProgramService;
        this.downloadService = downloadService;
        this.coachingProgramRepository = coachingProgramRepository;
    }

    public Step findById(Long stepId) {
        return validationHelper.validateStep(stepId);
    }

    public List<Step> addStepsToProgram(List<StepInputDto> inputDtos) {
        List<Step> savedSteps = new ArrayList<>();

        for (StepInputDto inputDto : inputDtos) {
            Long coachingProgramId = inputDto.getCoachingProgramId();

            CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

            List<Session> sessions = (inputDto.getSessionIds() != null) ? validationHelper.validateSessions(inputDto.getSessionIds()) : new ArrayList<>();
            List<Assignment> assignments = (inputDto.getAssignmentIds() != null) ? validationHelper.validateAssignments(inputDto.getAssignmentIds()) : new ArrayList<>();

            if (inputDto.getStepStartDate() == null || inputDto.getStepEndDate() == null) {
                throw new IllegalArgumentException("Step start date and end date cannot be null.");
            }

            Step step = StepMapper.toStepEntity(inputDto, coachingProgram, sessions, assignments);

            validateStepSequence(coachingProgramId, step);

            step.setCoachingProgram(coachingProgram);
            coachingProgram.getTimeline().add(step);

            Step savedStep = stepRepository.save(step);
            coachingProgramRepository.save(coachingProgram);

            coachingProgramService.updateProgramEndDate(coachingProgramId);

            savedSteps.add(savedStep);
        }

        return savedSteps;
    }

    public Step updateStep(Long stepId, StepInputDto inputDto) {

        Step existingStep = validationHelper.validateStep(stepId);

        if (inputDto.getStepName() != null) {
            existingStep.setStepName(inputDto.getStepName());
        }
        if (inputDto.getStepStartDate() != null) {
            existingStep.setStepStartDate(DateConverter.convertToLocalDate(inputDto.getStepStartDate()));
        }
        if (inputDto.getStepEndDate() != null) {
            existingStep.setStepEndDate(DateConverter.convertToLocalDate(inputDto.getStepEndDate()));
        }
        if (inputDto.getCompleted() != null) {
            existingStep.setCompleted(inputDto.getCompleted());
        }
        if (inputDto.getStepGoal() != null) {
            existingStep.setStepGoal(inputDto.getStepGoal());
        }
        if (inputDto.getSequence() != null) {
            validateStepSequence(existingStep.getCoachingProgram().getCoachingProgramId(), existingStep);
            existingStep.setSequence(inputDto.getSequence());
        }
        if (inputDto.getCoachingProgramId() != null) {
            CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
            existingStep.setCoachingProgram(coachingProgram);
        }
        if (inputDto.getSessionIds() != null && !inputDto.getSessionIds().isEmpty()) {
            List<Session> sessions = validationHelper.validateSessions(inputDto.getSessionIds());
            existingStep.setSession(sessions);
        }
        if (inputDto.getAssignmentIds() != null && !inputDto.getAssignmentIds().isEmpty()) {
            List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentIds());
            existingStep.setAssignment(assignments);
        }

        Step updatedStep = stepRepository.save(existingStep);

        coachingProgramService.updateProgramEndDate(existingStep.getCoachingProgram().getCoachingProgramId());

        return updatedStep;
    }
    private void validateStepSequence(Long coachingProgramId, Step newStep) {
        List<Step> existingSteps = stepRepository.findStepsByCoachingProgram(coachingProgramId);

        for (Step step : existingSteps) {
            if (step.getSequence().equals(newStep.getSequence())) {
                throw new IllegalArgumentException("A step with sequence " + newStep.getSequence() + " already exists in this program.");
            }

            if (step.getStepStartDate().isAfter(newStep.getStepStartDate()) && step.getSequence() < newStep.getSequence()) {
                throw new IllegalArgumentException("The step's start date conflicts with the provided sequence.");
            }
        }
    }

    public void deleteStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);

        CoachingProgram coachingProgram = step.getCoachingProgram();
        if (coachingProgram != null) {
            coachingProgramService.updateProgramEndDate(coachingProgram.getCoachingProgramId());
        }
        stepRepository.delete(step);
    }

    public Step markStepCompletionStatus(Long stepId, boolean isCompleted) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));
        step.setCompleted(isCompleted);
        return stepRepository.save(step);
    }

    public byte[] downloadFilesForStep(Long stepId) throws IOException {
        Step step = validationHelper.validateStep(stepId);
        return downloadService.downloadFilesForEntity(step);
    }


}