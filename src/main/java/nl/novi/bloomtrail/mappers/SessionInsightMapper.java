package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionInsightDto;
import nl.novi.bloomtrail.dtos.SessionInsightInputDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;

import java.util.List;

public class SessionInsightMapper {

    public static SessionInsightDto toDto(SessionInsight sessionInsight) {
        SessionInsightDto dto = new SessionInsightDto();

        dto.setSessionInsightId(sessionInsight.getSessionInsightId());
        dto.setAuthor(sessionInsight.getAuthor());
        dto.setCreatedAt(sessionInsight.getCreatedAt());
        dto.setDescription(sessionInsight.getDescription());
        dto.setSessionId(sessionInsight.getSession() != null ? sessionInsight.getSession().getSessionId() : null);

        List<File> files = sessionInsight.getFiles();
        if (files != null && !files.isEmpty()) {
            dto.setClientReflectionUrls(
                    files.stream()
                            .filter(f -> f.getContext() == FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION)
                            .map(f -> "/files/" + f.getFileId())
                            .toList()
            );

            dto.setCoachNotesUrls(
                    files.stream()
                            .filter(f -> f.getContext() == FileContext.SESSION_INSIGHTS_COACH_NOTES)
                            .map(f -> "/files/" + f.getFileId())
                            .toList()
            );
        }

        return dto;
    }

    public static SessionInsight toSessionInsightEntity(SessionInsightInputDto inputDto, Session session) {
        SessionInsight sessionInsight = new SessionInsight();

        sessionInsight.setAuthor(inputDto.getAuthor());
        sessionInsight.setDescription(inputDto.getDescription());
        sessionInsight.setSession(session);

        return sessionInsight;
    }


}
