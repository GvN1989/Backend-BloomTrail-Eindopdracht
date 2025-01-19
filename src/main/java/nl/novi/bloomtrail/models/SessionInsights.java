package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
public class SessionInsights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionInsightId;
    @Size(max = 500)
    @NotBlank
    private String clientReflection;
    @Size(max = 500)
    @NotBlank
    private String coachNotes;
    @NotBlank
    private String author;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToOne(mappedBy = "sessionInsights")
    private Session session;

    @OneToMany(mappedBy = "sessionInsight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    private String downloadUrl;

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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
        if (session != null && session.getSessionInsights() != this) {
            session.setSessionInsights(this);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
