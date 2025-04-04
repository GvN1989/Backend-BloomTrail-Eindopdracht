package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.helper.DateConverter;
import nl.novi.bloomtrail.helper.TimeConverter;

import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDto toSessionDto(Session session) {
        SessionDto dto = new SessionDto();

        dto.setSessionId(session.getSessionId());
        dto.setSessionName(session.getSessionName());
        dto.setCoach(session.getCoach());
        dto.setClient(session.getClient());
        dto.setSessionDate(session.getSessionDate());
        dto.setSessionTime(session.getSessionTime());
        dto.setLocation(session.getLocation());
        dto.setComment(session.getComment());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
        dto.setStepId(session.getStep() != null ? session.getStep().getStepId() : null);

        if (session.getSessionInsights() != null) {
            dto.setSessionInsightId(
                    session.getSessionInsights().stream()
                            .map(SessionInsight::getSessionInsightId)
                            .collect(Collectors.toList())
            );


        }

        if (session.getAssignment() != null) {
            dto.setAssignmentId(
                    session.getAssignment().stream()
                            .map(Assignment::getAssignmentId)
                            .collect(Collectors.toList())
            );


        }

        return dto;

    }

    public static Session toSessionEntity(SessionInputDto inputDto, Step step, List<SessionInsight> sessionInsights, List<Assignment> assignments) {
        if (inputDto == null) {
            throw new ForbiddenException("SessionInputDto cannot be null");
        }
        try {

        Session session = new Session();

        session.setSessionName(inputDto.getSessionName());
        session.setCoach(inputDto.getCoach());
        session.setClient(inputDto.getClient());
        session.setSessionDate(DateConverter.convertToLocalDate(inputDto.getSessionDate()));
        session.setSessionTime(TimeConverter.convertToLocalTime(inputDto.getSessionTime()));
        session.setLocation(inputDto.getLocation());
        session.setComment(inputDto.getComment());
        session.setStep(step);
        session.setSessionInsights(sessionInsights);
        session.setAssignment(assignments);

        return session;
        }catch (Exception e) {
            throw new ForbiddenException("Error mapping SessionInputDto to Session" + e);
        }

    }

}
