package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StepDto;
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

}
