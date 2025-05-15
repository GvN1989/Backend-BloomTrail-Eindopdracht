package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Assignment;

import java.util.List;


public class StepMapper {

    public static StepDto toStepDto(Step step) {
        StepDto dto = new StepDto();

        dto.setStepId(step.getStepId());
        dto.setStepName(step.getStepName());
        dto.setStepGoal(step.getStepGoal());
        dto.setCompleted(step.getCompleted());
        dto.setStepStartDate(step.getStepStartDate());
        dto.setStepEndDate(step.getStepEndDate());
        dto.setSequence(step.getSequence());
        dto.setCoachingProgramId(step.getCoachingProgram() != null ? step.getCoachingProgram().getCoachingProgramId() : null);

        if (step.getSessions() != null) {
            dto.setSessionIds(
                    step.getSessions().stream()
                            .map(Session::getSessionId)
                            .toList()
            );
        }

        if (step.getAssignments() != null) {
            dto.setAssignments(
                    step.getAssignments().stream()
                            .map(AssignmentMapper::toAssignmentDto)
                            .toList()
            );
        }

        return dto;
    }

    public static Step toStepEntity(StepInputDto inputDto, CoachingProgram coachingProgramId, List<Session> sessions, List<Assignment> assignments) {
        if (inputDto == null) {
            throw new ForbiddenException("StepInputDto cannot be null");
        }
        try {

        Step step = new Step();

        step.setStepName(inputDto.getStepName());
        step.setStepStartDate(inputDto.getStepStartDate());
        step.setStepEndDate(inputDto.getStepEndDate());
        step.setCompleted(inputDto.getCompleted());
        step.setStepGoal(inputDto.getStepGoal());
        step.setCoachingProgram(coachingProgramId);
        step.setSessions(sessions);
        step.setAssignments(assignments);

        return step;
        }catch (Exception e) {
            throw new ForbiddenException("Error mapping StepInputDto to Step" + e);
        }

    }

    public static void updateStepFromDto(Step step, StepInputDto dto) {
        if (dto.getStepName() != null) step.setStepName(dto.getStepName());
        if (dto.getStepGoal() != null) step.setStepGoal(dto.getStepGoal());
        if (dto.getStepStartDate() != null) {step.setStepStartDate(dto.getStepStartDate());}
        if (dto.getStepEndDate() != null) {step.setStepEndDate(dto.getStepEndDate());}
        if (dto.getCompleted() != null) step.setCompleted(dto.getCompleted());
    }
}
