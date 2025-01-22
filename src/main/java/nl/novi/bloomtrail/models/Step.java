package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "steps")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StepId;

    private String StepName;

    private Date stepStartDate;

    private Date stepEndDate;

    private Boolean completed;

    private String StepGoal;
    @NotNull
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "coaching_program_id", nullable = false)
    private CoachingProgram coachingProgram;

    @OneToMany
    @JoinColumn(name = "session_id", nullable = false)
    private List<Session> session = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "assignment_id", nullable = false)
    private List<Assignment> assignment = new ArrayList<>();

    public Long getStepId() {
        return StepId;
    }

    public void setStepId(Long stepId) {
        this.StepId = stepId;
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
        return StepId != null && StepId.equals(step.getStepId());
    }

    @Override
    public int hashCode() {
        return StepId != null ? StepId.hashCode() : 0;
    }

}
