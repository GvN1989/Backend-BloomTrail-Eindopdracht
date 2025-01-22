package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.mappers.StepMapper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepService {
    private final StepRepository stepRepository;

    private final EntityValidationHelper validationHelper;

    public StepService(StepRepository stepRepository, EntityValidationHelper validationHelper) {
        this.stepRepository = stepRepository;
        this.validationHelper = validationHelper;
    }

    public List<Step> findAll(){
        return stepRepository.findAll();
    }

    public Step findById(Long stepId){
        return validationHelper.validateStep(stepId);

    }
    public List<Step> findStepsByCoachingProgram(Long coachingProgramId) {
        List<Step> steps= stepRepository.findStepsByCoachingProgram(coachingProgramId);
        if(steps.isEmpty()) {
            throw new RecordNotFoundException("No steps found for CoachingProgram with ID: " + coachingProgramId);
        }
        return stepRepository.findStepsByCoachingProgram(coachingProgramId);

    }

    public Step addStepToProgram(StepInputDto inputDto) {
        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
        List<Session> sessions = validationHelper.validateSessions(inputDto.getSessionIds());
        List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentIds());

        Step step = StepMapper.toStepEntity(inputDto, coachingProgram, sessions, assignments);

        return stepRepository.save(step);
    }


    public Step updateStep (Long stepId, StepInputDto inputDto) {

        Step existingStep = validationHelper.validateStep(stepId);

        CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(inputDto.getCoachingProgramId());
        List<Session> sessions = validationHelper.validateSessions(inputDto.getSessionIds());
        List<Assignment> assignments = validationHelper.validateAssignments(inputDto.getAssignmentIds());

        existingStep.setStepName(inputDto.getStepName());
        existingStep.setStepStartDate(inputDto.getStepStartDate());
        existingStep.setStepEndDate(inputDto.getStepEndDate());
        existingStep.setCompleted(inputDto.getCompleted());
        existingStep.setStepGoal(inputDto.getStepGoal());
        existingStep.setSequence(inputDto.getSequence());
        existingStep.setCoachingProgram(coachingProgram);
        existingStep.setSession(sessions);
        existingStep.setAssignment(assignments);

        return stepRepository.save(existingStep);
    }

    public void deleteStep (Long stepId) {
        Step step = validationHelper.validateStep(stepId);
        stepRepository.delete(step);
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

    public Step markStepCompletionStatus(Long stepId, boolean isCompleted) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RecordNotFoundException("Step with ID " + stepId + " not found"));

        step.setCompleted(isCompleted);
        return stepRepository.save(step);
    }
}
