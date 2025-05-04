package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.helper.StepSequenceHelper;
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
    private final StepSequenceHelper stepSequenceHelper;
    private final CoachingProgramService coachingProgramService;
    private final DownloadService downloadService;
    private final CoachingProgramRepository coachingProgramRepository;

    public StepService(StepRepository stepRepository, ValidationHelper validationHelper, StepSequenceHelper stepSequenceHelper, CoachingProgramService coachingProgramService, DownloadService downloadService, CoachingProgramRepository coachingProgramRepository) {
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
        this.stepSequenceHelper = stepSequenceHelper;
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

        if (inputDtos == null || inputDtos.isEmpty()) {
            throw new BadRequestException("Step input list must not be empty.");
        }

        List<Step> savedSteps = new ArrayList<>();

        Long coachingProgramId = inputDtos.get(0).getCoachingProgramId();
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);

        for (StepInputDto inputDto : inputDtos) {
            validationHelper.validateStepCreationInput(inputDto);

            List<Session> sessions = (inputDto.getSessionIds() != null) ? validationHelper.validateSessions(inputDto.getSessionIds()) : new ArrayList<>();
            List<Assignment> assignments = (inputDto.getAssignmentIds() != null) ? validationHelper.validateAssignments(inputDto.getAssignmentIds()) : new ArrayList<>();

            Step step = StepMapper.toStepEntity(inputDto, coachingProgram, sessions, assignments);

            step.setCoachingProgram(coachingProgram);
            coachingProgram.getTimeline().add(step);

            Step savedStep = stepRepository.save(step);
            savedSteps.add(savedStep);
        }

            stepSequenceHelper.reorderStepsForProgram(coachingProgram);

            coachingProgramRepository.save(coachingProgram);
            coachingProgramService.updateProgramEndDate(coachingProgramId);

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
            existingStep.setStepStartDate(inputDto.getStepStartDate());
        }
        if (inputDto.getStepEndDate() != null) {
            existingStep.setStepEndDate(inputDto.getStepEndDate());
        }
        if (inputDto.getStepGoal() != null) {
            existingStep.setStepGoal(inputDto.getStepGoal());
        }
        if (inputDto.getCompleted() != null) {
            existingStep.setCompleted(inputDto.getCompleted());
        }

        Step updatedStep = stepRepository.save(existingStep);

        stepSequenceHelper.reorderStepsForProgram(coachingProgram);

        coachingProgramService.updateProgramEndDate(existingStep.getCoachingProgram().getCoachingProgramId());

        return updatedStep;
    }

    public void deleteStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);

        CoachingProgram coachingProgram = step.getCoachingProgram();
        if (coachingProgram != null) {
            coachingProgramService.updateProgramEndDate(coachingProgram.getCoachingProgramId());
        }
        stepRepository.delete(step);
    }

    public byte[] downloadFilesForStep(Long stepId) throws IOException {
        Step step = validationHelper.validateStep(stepId);
        return downloadService.downloadFilesForEntity(step);
    }


}