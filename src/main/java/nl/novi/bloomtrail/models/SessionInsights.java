package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;


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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_reflection_upload_id")
    private Upload clientReflectionUpload;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coach_notes_upload_id")
    private Upload coachNotesUpload;

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

    public Upload getClientReflectionUpload() {
        return clientReflectionUpload;
    }

    public void setClientReflectionUpload(Upload clientReflectionUpload) {
        this.clientReflectionUpload = clientReflectionUpload;
    }

    public Upload getCoachNotesUpload() {
        return coachNotesUpload;
    }

    public void setCoachNotesUpload(Upload coachNotesUpload) {
        this.coachNotesUpload = coachNotesUpload;
    }
}
