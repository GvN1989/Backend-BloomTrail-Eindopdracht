package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Getter
@Entity
@Table (name= "steps")
public class Step {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer stepId;
    private Date stepDate;
    private Boolean completed;
    private String goal;

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public void setStepDate(Date stepDate) {
        this.stepDate = stepDate;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
