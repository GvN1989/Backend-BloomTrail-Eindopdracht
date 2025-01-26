package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import nl.novi.bloomtrail.enums.FileContext;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Entity
@Table(name = "uploads")

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
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
    @JoinColumn(name = "assignment_id", nullable = true)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "strength_results_id", nullable = true)
    private StrengthResults strengthResults;

    @ManyToOne
    @JoinColumn(name = "sessionInsights_id", nullable = true)
    private SessionInsight sessionInsight;

    @OneToOne(mappedBy = "upload", cascade = CascadeType.ALL)
    private User user;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long uploadId) {
        this.fileId = uploadId;
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

    public StrengthResults getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(StrengthResults strengthResults) {
        this.strengthResults = strengthResults;
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
        if (strengthResults != null) relationCount++;
        if (sessionInsight != null) relationCount++;

        if (relationCount > 1) {
            throw new IllegalStateException("Upload can only belong to one context: Assignment, StrengthResults, or SessionInsights");
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
