package nl.novi.bloomtrail.dtos;

import java.util.Date;
import java.util.List;

public class StepDto {

    private Long StepId;

    private String StepName;

    private Date stepStartDate;

    private Date stepEndDate;

    private Boolean completed;

    private String stepGoal;

    private Integer sequence;

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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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
