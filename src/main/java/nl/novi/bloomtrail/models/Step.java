package nl.novi.bloomtrail.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table (name= "steps")
public class Step {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer stepId;
    private Date stepDate;
    private Boolean completed;
    private String goal;
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "strength_program_id", nullable = false)
    private StrengthProgram strengthProgram;

    @OneToMany
    @JoinColumn(name = "session_id", nullable = false)
    private List<Session> sessions = new ArrayList<>();

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public Date getStepDate() {
        return stepDate;
    }

    public void setStepDate(Date stepDate) {
        this.stepDate = stepDate;
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

    public StrengthProgram getStrengthProgram() {
        return strengthProgram;
    }

    public void setStrengthProgram(StrengthProgram strengthProgram) {
        this.strengthProgram = strengthProgram;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}
