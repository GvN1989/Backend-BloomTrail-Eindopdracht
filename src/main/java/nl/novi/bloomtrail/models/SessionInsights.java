package nl.novi.bloomtrail.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class SessionInsights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionInsightId;
    private String clientReflection;
    private String coachNotes;
    private String author;
    private LocalDate createdDate;
    private LocalTime createdTime;

    @OneToOne(mappedBy = "sessionInsights")
    private Session session;


    public Long getSessionInsightId() {
        return sessionInsightId;
    }

    public void setSessionInsightId(Long sessionInsightId) {
        this.sessionInsightId = sessionInsightId;
    }

    public String getClientReflection() {
        return clientReflection;
    }

    public void setClientReflection(String clientReflection) {
        this.clientReflection = clientReflection;
    }

    public String getCoachNotes() {
        return coachNotes;
    }

    public void setCoachNotes(String coachNotes) {
        this.coachNotes = coachNotes;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalTime createdTime) {
        this.createdTime = createdTime;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
