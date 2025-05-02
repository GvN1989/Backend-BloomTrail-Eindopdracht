package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
public class StepInputDto {
    private Long StepId;

    private String StepName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy",timezone = "UTC")
    private Date stepStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private Date stepEndDate;

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

    public Date getStepStartDate() {
        return stepStartDate;
    }

    public void setStepStartDate(Date stepStartDate) {
        this.stepStartDate = stepStartDate;
    }

    public Date getStepEndDate() {
        return stepEndDate;
    }

    public void setStepEndDate(Date stepEndDate) {
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
