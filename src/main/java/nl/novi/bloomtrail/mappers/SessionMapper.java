package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.enums.SessionStatus;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.SessionInsight;
import nl.novi.bloomtrail.models.Step;

import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDto toSessionDto (Session session) {
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
        dto.setStatus(session.getStatus() != null ? session.getStatus().toString() : null);

        if (session.getSessionInsights() != null) {
            dto.setSessionInsightId(
                    session.getSessionInsights().stream()
                            .map(SessionInsight::getSessionInsightId)
                            .collect(Collectors.toList())
            );


        }

        if (session.getAssignments() != null) {
            dto.setAssignmentId(
                    session.getAssignments().stream()
                            .map(Assignment::getAssignmentId)
                            .collect(Collectors.toList())
            );


        }

       return dto;

    }

    public static Session toSessionEntity (SessionInputDto inputDto, Step step, List<SessionInsight>sessionInsights, List<Assignment> assignments) {

        Session session = new Session();

        session.setSessionName(inputDto.getSessionName());
        session.setCoach(inputDto.getCoach());
        session.setClient(inputDto.getClient());
        session.setSessionDate(inputDto.getSessionDate());
        session.setSessionTime(inputDto.getSessionTime());
        session.setLocation(inputDto.getLocation());
        session.setComment(inputDto.getComment());
        session.setStep(step);
        session.setSessionInsights(sessionInsights);
        session.setAssignments(assignments);
        session.setStatus(inputDto.getStatus());

        return session;

    }

}
