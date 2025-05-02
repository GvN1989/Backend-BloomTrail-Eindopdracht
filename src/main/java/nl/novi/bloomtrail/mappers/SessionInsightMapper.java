package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionInsightDto;
import nl.novi.bloomtrail.dtos.SessionInsightInputDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;

import java.util.Collections;

public class SessionInsightMapper {

    public static SessionInsightDto toDto(SessionInsight sessionInsight) {
        SessionInsightDto dto = new SessionInsightDto();

        dto.setSessionInsightId(sessionInsight.getSessionInsightId());
        dto.setAuthor(sessionInsight.getAuthor());
        dto.setCreatedAt(sessionInsight.getCreatedAt());
        dto.setDescription(sessionInsight.getDescription());
        dto.setFileContext(sessionInsight.getFileContext().name());
        dto.setSessionId(sessionInsight.getSession() != null ? sessionInsight.getSession().getSessionId() : null);

        if (sessionInsight.getFiles() != null && !sessionInsight.getFiles().isEmpty()) {
            dto.setFileUrls(
                    sessionInsight.getFiles().stream()
                            .map(file -> "/files/" + file.getFileId())
                            .toList()
            );
        } else {
            dto.setFileUrls(Collections.emptyList());
        }

        return dto;
    }

    public static SessionInsight toSessionInsightEntity(SessionInsightInputDto inputDto, Session session) {
        SessionInsight sessionInsight = new SessionInsight();

        sessionInsight.setAuthor(inputDto.getAuthor());
        sessionInsight.setDescription(inputDto.getDescription());
        sessionInsight.setSession(session);

        if (inputDto.getFileContext() != null) {
            sessionInsight.setFileContext(inputDto.getFileContext());
        }

        return sessionInsight;
    }


}
