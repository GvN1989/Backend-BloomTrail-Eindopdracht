package nl.novi.bloomtrail.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name= "coaching_programs")
public class CoachingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coachingProgramId;
    private String coachingProgramName;
    private String goal;
    private Date startDate;
    private Date endDate;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User Client;
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User Coach;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "strength_results_id")
    private StrengthResults strengthResults;

    @OneToMany(mappedBy = "strengthProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> timeline = new ArrayList<>();


    public Long getCoachingProgramId() {
        return coachingProgramId;
    }

    public void setCoachingProgramId(Long coachingProgramId) {
        this.coachingProgramId = coachingProgramId;
    }

    public String getCoachingProgramName() {
        return coachingProgramName;
    }

    public void setCoachingProgramName(String coachingProgramName) {
        this.coachingProgramName = coachingProgramName;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public List<Step> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<Step> timeline) {
        this.timeline = timeline;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public StrengthResults getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(StrengthResults strengthResults) {
        this.strengthResults = strengthResults;
    }

    public User getClient() {
        return Client;
    }

    public void setClient(User client) {
        Client = client;
    }

    public User getCoach() {
        return Coach;
    }

    public void setCoach(User coach) {
        Coach = coach;
    }
}
