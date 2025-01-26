package nl.novi.bloomtrail.models;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private double progress = 0.0;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User Client;
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User Coach;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "results_id")
    private List <StrengthResults> strengthResults;

    @OneToMany(mappedBy = "coaching_program", cascade = CascadeType.ALL, orphanRemoval = true)
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
