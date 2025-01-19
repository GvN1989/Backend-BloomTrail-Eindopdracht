package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import nl.novi.bloomtrail.models.Step;

import java.util.Date;

public class StepInputDto {
    private Long StepId;

    @NotBlank
    @Size(max = 255)
    private String StepName;

    @JsonFormat(pattern = "dd-mm-yyy")
    @Future
    private Date stepStartDate;
    @JsonFormat(pattern = "dd-mm-yyy")
    @Future
    private Date stepEndDate;
    private Boolean completed;
    @NotBlank
    @Size(max = 500)
    private String goal;

    private Integer sequence;

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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

}
