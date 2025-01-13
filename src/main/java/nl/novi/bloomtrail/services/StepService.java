package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.StrengthProgram;
import nl.novi.bloomtrail.repositories.SessionRepository;
import nl.novi.bloomtrail.repositories.StepRepository;
import nl.novi.bloomtrail.repositories.StrengthProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepService {
    private final StepRepository stepRepository;

    private final StrengthProgramRepository strengthProgramRepository;

    private final StrengthProgramService strengthProgramService;

    public StepService(StepRepository stepRepository, StrengthProgramRepository strengthProgramRepository, StrengthProgramService strengthProgramService, SessionRepository sessionRepository) {
        this.stepRepository = stepRepository;
        this.strengthProgramRepository = strengthProgramRepository;
        this.strengthProgramService = strengthProgramService;
    }

    public List<Step> findAll(){
        return stepRepository.findAll();
    }

    public Step findById(Long stepId){
        return stepRepository.findById(stepId)
                .orElseThrow(()-> new RecordNotFoundException("Step with id: " + stepId + "not found"));

    }

    public List<Step> findStepsByStrengthProgram(Long strengthProgramId) {
        return stepRepository.findStepsByStrengthProgram(strengthProgramId);

    }

    public Step addStepToProgram(Long strengthProgramId, Step step) {
        StrengthProgram strengthProgram = strengthProgramRepository.findById(strengthProgramId)
                .orElseThrow(() -> new RecordNotFoundException("StrengthProgram with ID " + strengthProgramId + " not found"));

        validateStepSequence(strengthProgramId, step);

        step.setStrengthProgram(strengthProgram);
        stepRepository.save(step);

        strengthProgramService.updateProgramEndDate(strengthProgramId);

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

    private void validateStepSequence(Long strengthProgramId, Step newStep) {
        List<Step> existingSteps = stepRepository.findStepsByStrengthProgram(strengthProgramId);

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



    // retrieve or manage sessions per step
}
