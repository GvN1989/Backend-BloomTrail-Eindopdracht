package nl.novi.bloomtrail.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name= "Sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(name = "Session_Name")
    private String sessionName;

    @NotBlank
    private String coach;

    @NotBlank
    private String client;

    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    @NotNull
    private LocalDate sessionDate;

    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    @NotNull
    private LocalTime sessionTime;

    @NotBlank
    private String location;

    @Size(max = 255)
    private String comment;

    @CreationTimestamp
    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_insight_id")
    private SessionInsights sessionInsights;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true) //Is the cascadetype sufficient?
    private List<Assignment> assignments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public SessionInsights getSessionInsights() {
        return sessionInsights;
    }

    public void setSessionInsights(SessionInsights sessionInsights) {
        this.sessionInsights = sessionInsights;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }
}
