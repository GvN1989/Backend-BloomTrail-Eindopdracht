package nl.novi.bloomtrail.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "steps")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @NotNull(message = "Step name is required")
    @Size(max = 255)
    @Column(name = "step_name")
    private String StepName;

    @Column(name = "step_start_date")
    private LocalDate stepStartDate;

    @Column(name = "step_end_date")
    private LocalDate stepEndDate;

    @Column(nullable = false,name = "step_status")
    private Boolean completed = false;

    @Column(name = "step_goal")
    private String StepGoal;

    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "coaching_program_id", insertable = false, updatable = false)
    private CoachingProgram coachingProgram;

    @OneToMany
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private List<Assignment> assignments = new ArrayList<>();

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
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

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
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
