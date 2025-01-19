package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepService {
    private final StepRepository stepRepository;

    private final CoachingProgramRepository coachingProgramRepository;

    private final CoachingProgramService coachingProgramService;

    public StepService(StepRepository stepRepository, CoachingProgramRepository coachingProgramRepository, CoachingProgramService coachingProgramService, SessionRepository sessionRepository) {
        this.stepRepository = stepRepository;
        this.coachingProgramRepository = coachingProgramRepository;
        this.coachingProgramService = coachingProgramService;
    }

    public List<Step> findAll(){
        return stepRepository.findAll();
    }

    public Step findById(Long stepId){
        return stepRepository.findById(stepId)
                .orElseThrow(()-> new RecordNotFoundException("Step with id: " + stepId + "not found"));

    }

    public List<Step> findStepsByCoachingProgram(Long coachingProgramId) {
        List<Step> steps= stepRepository.findStepsByCoachingProgram(coachingProgramId);
        if(steps.isEmpty()) {
            throw new RecordNotFoundException("No steps found for CoachingProgram with ID: " + coachingProgramId);
        }
        return stepRepository.findStepsByCoachingProgram(coachingProgramId);

    }

    public Step addStepToProgram(Long coachingProgramId, Step step) {
        CoachingProgram coachingProgram = coachingProgramRepository.findById(coachingProgramId)
                .orElseThrow(() -> new RecordNotFoundException("CoachingProgram with ID " + coachingProgramId + " not found"));

        validateStepSequence(coachingProgramId, step);

        step.setCoachingProgram(coachingProgram);
        stepRepository.save(step);

        coachingProgramService.updateProgramEndDate(coachingProgramId);

        return step;
    }

    public Step saveStep (StepInputDto inputDto) {
        Step step= StepMapper.toStepEntity(inputDto);
        return stepRepository.save(step);
    }

    public Step updateStep (Long stepId, StepInputDto inputDto) {

        if (!stepRepository.existsById(stepId)) {
            throw new RecordNotFoundException("No step found with ID " + stepId);
        }

        Step step = Step.toStepEntity(inputDto);
        step.setStepId(stepId);
        return stepRepository.save(step);
    }

    public void deleteStep (Long stepId) {
        if (!stepRepository.existsById(stepId)) {
            throw new RecordNotFoundException("No step found with ID " + stepId);
        }

        stepRepository.deleteById(stepId);
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
