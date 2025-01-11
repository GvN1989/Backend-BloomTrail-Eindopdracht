package nl.novi.bloomtrail.models;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name= "Sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;
    private String coach;
    private String client;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private String location;
    private String comment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_insight_id")
    private SessionInsights sessionInsights;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;
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
}
