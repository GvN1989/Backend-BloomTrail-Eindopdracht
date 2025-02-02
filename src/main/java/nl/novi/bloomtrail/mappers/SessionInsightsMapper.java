package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionInsightDto;
import nl.novi.bloomtrail.dtos.SessionInsightInputDto;
import nl.novi.bloomtrail.exceptions.MappingException;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;

import java.util.List;
import java.util.stream.Collectors;

public class SessionInsightsMapper {

    public static SessionInsightDto toSessionInsightDto(SessionInsight sessionInsight) {
        SessionInsightDto dto = new SessionInsightDto();
        dto.setSessionInsightId(sessionInsight.getSessionInsightId());
        dto.setAuthor(sessionInsight.getAuthor());
        dto.setCreatedAt(sessionInsight.getCreatedAt());
        dto.setDescription(sessionInsight.getDescription());
        dto.setFileContext(sessionInsight.getFileContext() != null ? sessionInsight.getFileContext().toString() : null);
        dto.setSessionId(sessionInsight.getSession().getSessionId());
        dto.setFileUrls(
                sessionInsight.getFiles().stream()
                        .map(File::getUrl)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public static SessionInsight toSessionInsightEntity(SessionInsightInputDto inputDto, Session session, List<File> files) {
        if (inputDto == null) {
            throw new MappingException("SessionInsightInputDto cannot be null");
        }
        try {
            SessionInsight entity = new SessionInsight();
            entity.setAuthor(inputDto.getAuthor());
            entity.setDescription(inputDto.getDescription());
            entity.setFileContext(inputDto.getFileContext());
            entity.setSession(session);
            entity.setFiles(files);
            return entity;
        } catch (Exception e) {
            throw new MappingException("Error mapping SessionInsightInputDto to SessionInsight", e);
        }
    }
}
