package nl.novi.bloomtrail.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class AssignmentDto {

    private Long assignmentId;
    private String description;
    private Long sessionId;
    private Long stepId;
    private List<Long> fileIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
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

    public List<Long> getFileId() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileId) {
        this.fileIds = fileId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }


}
