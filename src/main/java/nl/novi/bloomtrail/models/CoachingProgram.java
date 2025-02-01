package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
@Table(name= "coaching_programs")
public class CoachingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coaching_program_id")
    private Long coachingProgramId;

    @NotNull(message = "The coaching program cannot be null")
    private String coachingProgramName;
    private String goal;

    @NotNull(message = "Start date cannot be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @NotNull(message = "End date cannot be null")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private double progress = 0.0;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "strengt_results_id")
    private List <StrengthResults> strengthResults;

    @OneToMany(mappedBy = "coachingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getCoach() {
        return coach;
    }

    public void setCoach(User coach) {
        this.coach = coach;
    }

    public List<StrengthResults> getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(List<StrengthResults> strengthResults) {
        this.strengthResults = strengthResults;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
