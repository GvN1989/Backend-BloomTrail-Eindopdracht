package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "steps")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stepId;

    private String StepName;

    private Date stepStartDate;

    private Date stepEndDate;

    private Boolean completed;

    private String StepGoal;
    @NotNull
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "coaching_program_id", insertable = false, updatable = false)
    private CoachingProgram coachingProgram;

    @OneToMany
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private List<Session> session = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private List<Assignment> assignment = new ArrayList<>();

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
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

    public void setStepEndDate(Date stepDate) {
        this.stepEndDate = stepDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getStepGoal() {
        return StepGoal;
    }

    public void setStepGoal(String goal) {
        this.StepGoal = goal;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public CoachingProgram getCoachingProgram() {
        return coachingProgram;
    }

    public void setCoachingProgram(CoachingProgram coachingProgram) {
        this.coachingProgram = coachingProgram;
    }

    public List<Session> getSession() {
        return session;
    }

    public void setSession(List<Session> session) {
        this.session = session;
    }

    public List<Assignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<Assignment> assignment) {
        this.assignment = assignment;
    }

    public String getStepName() {
        return StepName;
    }

    public void setStepName(String stepName) {
        StepName = stepName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(stepId, step.stepId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepId);
    }

}
