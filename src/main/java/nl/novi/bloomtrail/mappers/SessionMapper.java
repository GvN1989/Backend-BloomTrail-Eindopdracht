package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.SessionDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;

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

        if (session.getStep() != null) {
            dto.setStepId(session.getStep().getStepId());
        }

        if (session.getSessionInsight() != null) {
            dto.setSessionInsight(SessionInsightMapper.toDto(session.getSessionInsight()));
        }

        return dto;

    }

    public static Session toSessionEntity(SessionInputDto inputDto, Step step) {
        if (inputDto == null) {
            throw new ForbiddenException("SessionInputDto cannot be null");
        }
        try {

        Session session = new Session();

        session.setSessionName(inputDto.getSessionName());
        session.setCoach(inputDto.getCoach());
        session.setClient(inputDto.getClient());
        session.setSessionDate(inputDto.getSessionDate());
        session.setSessionTime(inputDto.getSessionTime());
        session.setLocation(inputDto.getLocation());
        session.setComment(inputDto.getComment());
        session.setStep(step);

        return session;

        }catch (Exception e) {
            throw new ForbiddenException("Error mapping SessionInputDto to Session" + e);
        }
    }

    public static void updateSessionFromDto(Session session, SessionInputDto inputDto) {
        if (inputDto.getSessionName() != null) {
            session.setSessionName(inputDto.getSessionName());
        }
        if (inputDto.getSessionDate() != null) {
            session.setSessionDate(inputDto.getSessionDate());
        }
        if (inputDto.getSessionTime() != null) {
            session.setSessionTime(inputDto.getSessionTime());
        }
        if (inputDto.getLocation() != null) {
            session.setLocation(inputDto.getLocation());
        }
        if (inputDto.getComment() != null) {
            session.setComment(inputDto.getComment());
        }

    }

}
