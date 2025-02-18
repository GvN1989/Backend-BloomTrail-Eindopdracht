package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StepDto;
import nl.novi.bloomtrail.dtos.StepInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Assignment;

import java.util.List;
import java.util.stream.Collectors;

public class StepMapper {

    public static StepDto toStepDto(Step step) {
        StepDto dto = new StepDto();

        dto.setStepId(step.getStepId());
        dto.setStepName(step.getStepName());
        dto.setCompleted(step.getCompleted());
        dto.setStepStartDate(step.getStepStartDate());
        dto.setStepEndDate(step.getStepEndDate());
        dto.setCoachingProgramId(step.getCoachingProgram() != null ? step.getCoachingProgram().getCoachingProgramId() : null);

        if (step.getSession() != null) {
            dto.setSessionIds(
                    step.getSession().stream()
                            .map(Session::getSessionId)
                            .collect(Collectors.toList())
            );
        }

        if (step.getAssignment() != null) {
            dto.setAssignmentIds(
                    step.getAssignment().stream()
                            .map(Assignment::getAssignmentId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static Step toStepEntity (StepInputDto inputDto, CoachingProgram coachingProgramId, List<Session> sessions, List<Assignment> assignments) {

        Step step = new Step();

        step.setStepName(inputDto.getStepName());
        step.setStepStartDate(inputDto.getStepStartDate());
        step.setStepEndDate(inputDto.getStepEndDate());
        step.setCompleted(inputDto.getCompleted());
        step.setStepGoal(inputDto.getStepGoal());
        step.setSequence(inputDto.getSequence());
        step.setCoachingProgram(coachingProgramId);
        step.setSession(sessions);
        step.setAssignment(assignments);

        return step;

    }

}
