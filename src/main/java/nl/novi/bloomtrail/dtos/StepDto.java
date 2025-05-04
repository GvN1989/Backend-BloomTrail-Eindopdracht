package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;
import org.springframework.expression.spel.ast.Assign;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class StepDto {

    private Long StepId;
    private String StepName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate stepStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate stepEndDate;
    private Boolean completed;
    private String stepGoal;
    private Integer sequence;
    private Long coachingProgramId;
    private List<Long> sessionIds;
    private List<AssignmentDto> assignments;

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

    public List<AssignmentDto> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentDto> assignments) {
        this.assignments = assignments;
    }
}
