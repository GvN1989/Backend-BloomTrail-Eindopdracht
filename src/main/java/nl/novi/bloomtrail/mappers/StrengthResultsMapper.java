package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.StrengthResultsDto;
import nl.novi.bloomtrail.dtos.StrengthResultsInputDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.StrengthResults;
import nl.novi.bloomtrail.models.User;

public class StrengthResultsMapper {


    public static StrengthResultsDto toStrengthResultDto(StrengthResults strengthResults) {

        StrengthResultsDto dto = new StrengthResultsDto();

        dto.setResultsId(strengthResults.getResultsId());
        dto.setUsername(strengthResults.getUser().getUsername());
        dto.setCreatedAt(strengthResults.getCreatedAt());
        dto.setUpdatedAt(strengthResults.getUpdatedAt());
        dto.setSummary(strengthResults.getSummary());
        dto.setTopStrengthNames(strengthResults.getTopStrengthNames());

        return dto;
    }

    public static StrengthResults toStrengthResultsEntity(StrengthResultsInputDto inputDto, User user) {
        if (inputDto == null) {
            throw new ForbiddenException("StrengthResultsInputDto cannot be null");
        }
        try {
        StrengthResults strengthResults = new StrengthResults();
        strengthResults.setUser(user);
        strengthResults.setSummary(inputDto.getSummary());
        strengthResults.setTopStrengthNames(inputDto.getTopStrengthNames());


        return strengthResults;
        } catch (Exception e) {
            throw new ForbiddenException("Error mapping StrengthResultsInputDto to StrengthResults" + e);
        }
    }

}
