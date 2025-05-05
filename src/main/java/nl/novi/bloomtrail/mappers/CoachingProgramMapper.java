package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.CoachingProgramDto;
import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.dtos.CoachingProgramUpdateDto;
import nl.novi.bloomtrail.dtos.SimpleCoachingProgramDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.User;

import java.util.Collections;
import java.util.stream.Collectors;

public class CoachingProgramMapper {

    public static CoachingProgramDto toCoachingProgramDto(CoachingProgram coachingProgram) {
        CoachingProgramDto dto = new CoachingProgramDto();

        dto.setCoachingProgramId(coachingProgram.getCoachingProgramId());
        dto.setCoachingProgramName(coachingProgram.getCoachingProgramName());
        dto.setGoal(coachingProgram.getGoal());
        dto.setStartDate(coachingProgram.getStartDate());
        dto.setEndDate(coachingProgram.getEndDate());
        dto.setClientUsername(coachingProgram.getClient() != null ? coachingProgram.getClient().getUsername() : null);
        dto.setCoachUsername(coachingProgram.getCoach() != null ? coachingProgram.getCoach().getUsername() : null);

        if (coachingProgram.getTimeline() != null) {
            dto.setTimeline(
                    coachingProgram.getTimeline().stream()
                            .map(StepMapper::toStepDto)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setTimeline(Collections.emptyList());
        }

        return dto;
    }

    public static SimpleCoachingProgramDto toSimpleDto(CoachingProgram program) {
        return new SimpleCoachingProgramDto(
                program.getCoachingProgramId(),
                program.getCoachingProgramName(),
                program.getClient() != null ? program.getClient().getUsername() : null,
                program.getCoach() != null ? program.getCoach().getUsername() : null
        );
    }

    public static CoachingProgram toCoachingProgramEntity(CoachingProgramInputDto inputDto, User client, User coach) {
        if (inputDto == null) {
            throw new ForbiddenException("CoachingProgramInputDto cannot be null");
        }
        try{

        CoachingProgram coachingProgram = new CoachingProgram();

        coachingProgram.setCoachingProgramName(inputDto.getCoachingProgramName());
        coachingProgram.setGoal(inputDto.getGoal());
        coachingProgram.setStartDate(inputDto.getStartDate());
        coachingProgram.setEndDate(inputDto.getEndDate());
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

        return coachingProgram;
        } catch (Exception e) {
            throw new ForbiddenException("Error mapping CoachingProgramInputDto to CoachingProgram" + e);
        }
    }

    public static void updateCoachingProgramDto(CoachingProgram program, CoachingProgramUpdateDto dto) {

        if (dto.getCoachingProgramName() != null) {
            program.setCoachingProgramName(dto.getCoachingProgramName());
        }

        if (dto.getGoal() != null) {
            program.setGoal(dto.getGoal());
        }

        if (dto.getStartDate() != null) {
            program.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            program.setEndDate(dto.getEndDate());
        }

    }
}
