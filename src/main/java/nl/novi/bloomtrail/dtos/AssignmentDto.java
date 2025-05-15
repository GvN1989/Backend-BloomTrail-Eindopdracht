package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class AssignmentDto {

    private Long assignmentId;
    private String description;
    private Long stepId;
    @JsonProperty("assignment files")
    private List<String> fileUrls;
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
    public List<String> getFileUrls() {
        return fileUrls;
    }
    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
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
