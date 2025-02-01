package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class StepService {
    private final StepRepository stepRepository;
    private final EntityValidationHelper validationHelper;
    private final CoachingProgramService coachingProgramService;
    private final AssignmentRepository assignmentRepository;
    private final DownloadService downloadService;

    private final CoachingProgramRepository coachingProgramRepository;

    public StepService(StepRepository stepRepository, EntityValidationHelper validationHelper, CoachingProgramService coachingProgramService, AssignmentRepository assignmentRepository, DownloadService downloadService, CoachingProgramRepository coachingProgramRepository) {
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
        this.coachingProgramService = coachingProgramService;
        this.assignmentRepository = assignmentRepository;
        this.downloadService = downloadService;
        this.coachingProgramRepository = coachingProgramRepository;
    }

    public Step findById(Long stepId) {
        return validationHelper.validateStep(stepId);
    }

    public Step addStepToProgram(StepInputDto inputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
        List<Session> sessions = validationHelper.validateSessions(inputDto.getSessionIds());
        List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentIds());

        if (inputDto.getStepStartDate() == null || inputDto.getStepEndDate() == null) {
            throw new IllegalArgumentException("Step start date and end date cannot be null.");
        }

        Step step = StepMapper.toStepEntity(inputDto, coachingProgram, sessions, assignments);
        Step savedStep = stepRepository.save(step);

        coachingProgramService.assignStepToCoachingProgram(inputDto.getCoachingProgramId(), savedStep);

        coachingProgramRepository.flush();

        coachingProgramService.updateProgramEndDate(inputDto.getCoachingProgramId());

        return savedStep;
    }

    public Step updateStep(Long stepId, StepInputDto inputDto) {

        Step existingStep = validationHelper.validateStep(stepId);

        if (inputDto.getStepName() != null) {
            existingStep.setStepName(inputDto.getStepName());
        }
        if (inputDto.getStepStartDate() != null) {
            existingStep.setStepStartDate(inputDto.getStepStartDate());
        }
        if (inputDto.getStepEndDate() != null) {
            existingStep.setStepEndDate(inputDto.getStepEndDate());
        }
        if (inputDto.getCompleted() != null) {
            existingStep.setCompleted(inputDto.getCompleted());
        }
        if (inputDto.getStepGoal() != null) {
            existingStep.setStepGoal(inputDto.getStepGoal());
        }
        if (inputDto.getSequence() != null) {
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

        coachingProgramService.updateProgramEndDate(inputDto.getCoachingProgramId());

        return updatedStep;
    }

    public void deleteStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);

        CoachingProgram coachingProgram = step.getCoachingProgram();
        if (coachingProgram != null) {
            coachingProgramService.updateProgramEndDate(coachingProgram.getCoachingProgramId()); }
        stepRepository.delete(step);
    }
    public Step markStepCompletionStatus(Long stepId, boolean isCompleted) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));
        step.setCompleted(isCompleted);
        return stepRepository.save(step);
    }

    public Step assignAssignmentToStep(Long stepId, Long assignmentId) {
        Step step = validationHelper.validateStep(stepId);
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        if (step.getAssignment().contains(assignment)) {
            throw new IllegalArgumentException("Assignment is already associated with the step.");}

        assignment.setStep(step);
        step.getAssignment().add(assignment);
        assignmentRepository.save(assignment);
        return stepRepository.save(step); }

    public byte[] downloadFilesForStep(Long stepId) throws IOException {
        Step step = validationHelper.validateStep(stepId);
        return downloadService.downloadFilesForEntity(step);
    }
}