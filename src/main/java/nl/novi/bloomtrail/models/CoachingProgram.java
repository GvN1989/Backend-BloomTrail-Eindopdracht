package nl.novi.bloomtrail.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "coaching_programs")
public class CoachingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coaching_program_id")
    private Long coachingProgramId;

    @NotNull(message = "The coaching program cannot be null")
    @Column(name = "coaching_program_name")
    private String coachingProgramName;
    @Column(name = "goal")
    private String goal;

    @NotNull(message = "Start date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "progress", nullable = false)
    private double progress = 0.0;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @OneToMany(mappedBy = "coachingProgram", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
