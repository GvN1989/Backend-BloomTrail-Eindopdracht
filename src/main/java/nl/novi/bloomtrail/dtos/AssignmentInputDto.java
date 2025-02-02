package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AssignmentInputDto {

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    private Long sessionId;

    @NotNull(message = "StepId is required")
    private Long stepId;

    public boolean isValid() {
        return stepId != null;
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

    public Long getStepId() {
        return stepId;
    }
    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

}
