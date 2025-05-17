package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import nl.novi.bloomtrail.enums.FileContext;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "files")

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    private String originalFilename;
    private String fileType;
    private String url;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Enumerated(EnumType.STRING)
    @NotNull
    private FileContext context;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "session_insight_id")
    private SessionInsight sessionInsight;

    @OneToOne(mappedBy = "profilePicture")
    private User user;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long uploadId) {
        this.fileId = uploadId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public SessionInsight getSessionInsights() {
        return sessionInsight;
    }

    public void setSessionInsights(SessionInsight sessionInsight) {
        this.sessionInsight = sessionInsight;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public FileContext getContext() {
        return context;
    }

    public void setContext(FileContext context) {
        this.context = context;
    }

    @PostPersist
    @PostUpdate
    private void validateRelationships() {
        int relationCount = 0;
        if (assignment != null) relationCount++;
        if (sessionInsight != null) relationCount++;

        if (relationCount > 1) {
            throw new IllegalStateException("Upload can only belong to one context: Assignment or SessionInsights");
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
