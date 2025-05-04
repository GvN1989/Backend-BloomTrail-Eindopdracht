package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
public class StepInputDto {
    private Long StepId;

    private String StepName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate stepStartDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate stepEndDate;

    @NotNull(message = "Field 'completed' must not be null.")
    private Boolean completed;

    private String stepGoal;

    private Long coachingProgramId;

    private List<Long> sessionIds;

    private List<Long> assignmentIds;

    public Long getStepId() {
        return StepId;
    }

    public void setStepId(Long stepId) {
        StepId = stepId;
    }

    public String getStepName() {
        return StepName;
    }

    public void setStepName(String stepName) {
        StepName = stepName;
    }

    public LocalDate getStepStartDate() {
        return stepStartDate;
    }

    public void setStepStartDate(LocalDate stepStartDate) {
        this.stepStartDate = stepStartDate;
    }

    public LocalDate getStepEndDate() {
        return stepEndDate;
    }

    public void setStepEndDate(LocalDate stepEndDate) {
        this.stepEndDate = stepEndDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(String stepGoal) {
        this.stepGoal = stepGoal;
    }

    public Long getCoachingProgramId() {
        return coachingProgramId;
    }

    public void setCoachingProgramId(Long coachingProgramId) {
        this.coachingProgramId = coachingProgramId;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Long> getAssignmentIds() {
        return assignmentIds;
    }

    public void setAssignmentIds(List<Long> assignmentIds) {
        this.assignmentIds = assignmentIds;
    }
}
