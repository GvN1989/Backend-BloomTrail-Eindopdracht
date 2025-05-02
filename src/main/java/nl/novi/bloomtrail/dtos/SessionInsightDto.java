package nl.novi.bloomtrail.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class SessionInsightDto {

    private Long sessionInsightId;
    private String author;
    private LocalDateTime createdAt;
    private String description;
    private Long sessionId;
    private List<String> clientReflectionUrls;
    private List<String> coachNotesUrls;
    private List<String> fileUrls;

    public Long getSessionInsightId() {
        return sessionInsightId;
    }

    public void setSessionInsightId(Long sessionInsightId) {
        this.sessionInsightId = sessionInsightId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public List<String> getClientReflectionUrls() {
        return clientReflectionUrls;
    }

    public void setClientReflectionUrls(List<String> clientReflectionUrls) {
        this.clientReflectionUrls = clientReflectionUrls;
    }

    public List<String> getCoachNotesUrls() {
        return coachNotesUrls;
    }

    public void setCoachNotesUrls(List<String> coachNotesUrls) {
        this.coachNotesUrls = coachNotesUrls;
    }
}
