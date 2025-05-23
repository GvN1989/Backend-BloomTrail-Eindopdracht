package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", unique = true, nullable = false)
    private Long sessionId;
    @Column(name = "session_name")
    @NotBlank
    private String sessionName;
    @NotBlank
    private String coach;
    @NotBlank
    private String client;
    @Column(name = "session_date")
    @NotNull
    private LocalDate sessionDate;
    @Column(name = "session_time")
    @NotNull
    private LocalTime sessionTime;
    @NotBlank
    private String location;
    private String comment;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private SessionInsight sessionInsight;

    @ManyToOne
    @JoinColumn(name = "step_id")
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

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public SessionInsight getSessionInsight() {
        return sessionInsight;
    }

    public void setSessionInsight(SessionInsight sessionInsight) {
        this.sessionInsight = sessionInsight;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

}
