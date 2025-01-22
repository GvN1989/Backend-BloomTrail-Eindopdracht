package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.CoachingProgramDto;
import nl.novi.bloomtrail.dtos.CoachingProgramInputDto;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.User;

import java.util.stream.Collectors;

public class CoachingProgramMapper {

    public static CoachingProgramDto coachingProgramDto(CoachingProgram coachingProgram){
        CoachingProgramDto dto = new CoachingProgramDto();

        dto.setCoachingProgramId(coachingProgram.getCoachingProgramId());
        dto.setCoachingProgramName(coachingProgram.getCoachingProgramName());
        dto.setGoal(coachingProgram.getGoal());
        dto.setStartDate(coachingProgram.getStartDate());
        dto.setEndDate(coachingProgram.getEndDate());
        dto.setClientUsername(coachingProgram.getClient() != null ? coachingProgram.getClient().getUsername() : null);
        dto.setCoachUsername(coachingProgram.getCoach() != null ? coachingProgram.getCoach().getUsername() : null);

        if (coachingProgram.getStrengthResults() != null) {
            dto.setStrengthResultUrls(
                    coachingProgram.getStrengthResults().getUploads().stream()
                            .map(File::getUrl)
                            .collect(Collectors.toList())
            );
        }

        dto.setTimeline(
                coachingProgram.getTimeline().stream()
                        .map(StepMapper::toStepDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public static CoachingProgram toCoachingProgramEntity(CoachingProgramInputDto inputDto,  User client, User coach) {

        CoachingProgram coachingProgram= new CoachingProgram();

        coachingProgram.setCoachingProgramName(inputDto.getCoachingProgramName());
        coachingProgram.setGoal(inputDto.getGoal());
        coachingProgram.setStartDate(inputDto.getStartDate());
        coachingProgram.setEndDate(inputDto.getEndDate());
        coachingProgram.setClient(client);
        coachingProgram.setCoach(coach);

        return coachingProgram;


    }

}
