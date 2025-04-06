package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.dtos.StepReorderDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.helper.ValidationHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StepService {

    private final StepRepository stepRepository;
    private final ValidationHelper validationHelper;
    private final CoachingProgramService coachingProgramService;
    private final DownloadService downloadService;
    private final CoachingProgramRepository coachingProgramRepository;

    public StepService(StepRepository stepRepository, ValidationHelper validationHelper, CoachingProgramService coachingProgramService, DownloadService downloadService, CoachingProgramRepository coachingProgramRepository) {
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
        this.coachingProgramService = coachingProgramService;
        this.downloadService = downloadService;
        this.coachingProgramRepository = coachingProgramRepository;
    }

    public Step findById(Long stepId) {
        return validationHelper.validateStep(stepId);
    }

    public List<Step> getStepsForUserAndProgram(String username, Long programId) {
        validationHelper.validateUser(username);

        CoachingProgram program = coachingProgramRepository
                .findByCoachingProgramIdAndClientUsername(programId, username)
                .orElseThrow(() -> new NotFoundException("Program not found for user"));

        List<Step> steps = stepRepository.findByCoachingProgram(program);

        if (steps.isEmpty()) {
            throw new NotFoundException("No steps found for CoachingProgram with ID: " + programId);
        }

        return steps;
    }

    public List<Step> addStepsToProgram(List<StepInputDto> inputDtos) {
        List<Step> savedSteps = new ArrayList<>();

        for (StepInputDto inputDto : inputDtos) {
            Long coachingProgramId = inputDto.getCoachingProgramId();

            CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

            List<Session> sessions = (inputDto.getSessionIds() != null) ? validationHelper.validateSessions(inputDto.getSessionIds()) : new ArrayList<>();
            List<Assignment> assignments = (inputDto.getAssignmentIds() != null) ? validationHelper.validateAssignments(inputDto.getAssignmentIds()) : new ArrayList<>();

            validationHelper.validateStepCreationInput(inputDto);

            Step step = StepMapper.toStepEntity(inputDto, coachingProgram, sessions, assignments);

            if (inputDto.getSequence() == null) {
                int max = stepRepository.findByCoachingProgram(coachingProgram).stream()
                        .mapToInt(Step::getSequence)
                        .max()
                        .orElse(0);
                step.setSequence(max + 1);
            } else {
                step.setSequence(inputDto.getSequence());
            }

            validationHelper.validateStepSequence(coachingProgram, step);

            step.setCoachingProgram(coachingProgram);
            coachingProgram.getTimeline().add(step);

            Step savedStep = stepRepository.save(step);
            coachingProgramRepository.save(coachingProgram);

            coachingProgramService.updateProgramEndDate(coachingProgramId);

            savedSteps.add(savedStep);
        }

        return savedSteps;
    }

    public Step updateStepDetails(Long stepId, StepInputDto inputDto) {

        Step existingStep = validationHelper.validateStep(stepId);
        CoachingProgram coachingProgram = existingStep.getCoachingProgram();

        validationHelper.validateCoachOwnsProgramOrIsAdmin(coachingProgram);

        if (inputDto.getStepName() != null) {
            existingStep.setStepName(inputDto.getStepName());
        }
        if (inputDto.getStepStartDate() != null) {
            existingStep.setStepStartDate(DateConverter.convertToLocalDate(inputDto.getStepStartDate()));
        }
        if (inputDto.getStepEndDate() != null) {
            existingStep.setStepEndDate(DateConverter.convertToLocalDate(inputDto.getStepEndDate()));
        }
        if (inputDto.getStepGoal() != null) {
            existingStep.setStepGoal(inputDto.getStepGoal());
        }

        Step updatedStep = stepRepository.save(existingStep);

        coachingProgramService.updateProgramEndDate(existingStep.getCoachingProgram().getCoachingProgramId());

        return updatedStep;
    }

    public List<Step> reorderStepSequence(List<StepReorderDto> reorderDtos) {
        if (reorderDtos == null || reorderDtos.isEmpty()) {
            throw new IllegalArgumentException("No steps provided for reordering.");
        }
        List<Step> updatedSteps = new ArrayList<>();

        for (StepReorderDto dto : reorderDtos) {
            Step step = validationHelper.validateStep(dto.getStepId());
            CoachingProgram coachingProgram = step.getCoachingProgram();

            validationHelper.validateCoachOwnsProgramOrIsAdmin(coachingProgram);

            step.setSequence(dto.getNewSequence());
            validationHelper.validateStepSequence(coachingProgram, step);

            updatedSteps.add(step);
        }

        return stepRepository.saveAll(updatedSteps);
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
                .orElseThrow(() -> new NotFoundException("Step with ID " + stepId + " not found"));
        step.setCompleted(isCompleted);
        return stepRepository.save(step);
    }

    public byte[] downloadFilesForStep(Long stepId) throws IOException {
        Step step = validationHelper.validateStep(stepId);
        return downloadService.downloadFilesForEntity(step);
    }


}