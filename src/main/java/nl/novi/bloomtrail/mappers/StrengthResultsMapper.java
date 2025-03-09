package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StrengthResultsDto;
import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.exceptions.MappingException;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.StrengthResults;

import java.util.stream.Collectors;

public class StrengthResultsMapper {


    public static StrengthResultsDto toStrengthResultDto(StrengthResults strengthResults) {

        StrengthResultsDto dto = new StrengthResultsDto();

        dto.setResultsId(strengthResults.getResultsId());
        dto.setFilename(strengthResults.getFilename());
        dto.setCreatedAt(strengthResults.getCreatedAt());
        dto.setUpdatedAt(strengthResults.getUpdatedAt());
        dto.setSummary(strengthResults.getSummary());
        dto.setStrengthResultsFilePath(strengthResults.getStrengthResultsFilePath());
        dto.setTopStrengthNames(strengthResults.getTopStrengthNames());

        if (strengthResults.getFiles() != null) {
            dto.setFileUrls(
                    strengthResults.getFiles().stream()
                            .map(file -> file.getUrl())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static StrengthResults toStrengthResultsEntity(StrengthResultsInputDto inputDto, CoachingProgram coachingProgram) {
        if (inputDto == null) {
            throw new MappingException("StrengthResultsInputDto cannot be null");
        }
        try {
        StrengthResults strengthResults = new StrengthResults();

        strengthResults.setFilename(inputDto.getFilename());
        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());
        strengthResults.setCoachingProgram(coachingProgram);

        return strengthResults;
        } catch (Exception e) {
            throw new MappingException("Error mapping StrengthResultsInputDto to StrengthResults", e);
        }



    }

}
