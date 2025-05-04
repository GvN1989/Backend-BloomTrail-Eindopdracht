package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
@NoArgsConstructor
public class SessionInputDto {

    private Long sessionId;
    @NotBlank
    private String sessionName;
    @NotBlank
    private String coach;
    @NotBlank
    private String client;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Future
    private LocalDate sessionDate;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime sessionTime;
    private String location;
    private String comment;
    @Future
    private LocalDateTime createdAt;
    @Future
    private LocalDateTime updatedAt;
    private SessionInsightInputDto sessionInsight;
    private Long StepId;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getStepId() {
        return StepId;
    }

    public void setStepId(Long stepId) {
        StepId = stepId;
    }

    public SessionInsightInputDto getSessionInsight() {
        return sessionInsight;
    }

    public void setSessionInsight(SessionInsightInputDto sessionInsight) {
        this.sessionInsight = sessionInsight;
    }
}
