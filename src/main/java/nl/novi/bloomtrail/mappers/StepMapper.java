package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.Step;

public class StepMapper {

    public static StepDto toStepDto(Step step) {
        StepDto stepDto = new StepDto();
        stepDto.setStepId(step.getStepId());
        stepDto.setStepName(step.getStepName());
        stepDto.setCompleted(step.getCompleted());
        stepDto.setStepStartDate(step.getStepStartDate());
        stepDto.setStepEndDate(step.getStepEndDate());

        return stepDto;
    }

    public static Step toStepEntity (StepInputDto inputDto) {

        Step step = new Step();

        step.setStepName(inputDto.getStepName());
        step.setStepStartDate(inputDto.getStepStartDate());
        step.setStepEndDate(inputDto.getStepEndDate());
        step.setCompleted(inputDto.getCompleted());
        step.setStepGoal(inputDto.getStepGoal());
        step.setSequence(inputDto.getSequence());

        return step;

    }

}
