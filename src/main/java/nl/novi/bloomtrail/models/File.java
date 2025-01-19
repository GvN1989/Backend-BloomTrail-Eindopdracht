package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Table(name = "uploads")

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uploadId;
    private String fileType;
    private String url;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UploadContext context;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = true)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "strength_results_id", nullable = true)
    private StrengthResults strengthResults;

    @ManyToOne
    @JoinColumn(name = "sessionInsights_id", nullable = true)
    private SessionInsights sessionInsights;


    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
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

    public SessionInsights getSessionInsights() {
        return sessionInsights;
    }

    public void setSessionInsights(SessionInsights sessionInsights) {
        this.sessionInsights = sessionInsights;
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

    public UploadContext getContext() {
        return context;
    }

    public void setContext(UploadContext context) {
        this.context = context;
    }

    @PostPersist
    @PostUpdate
    private void validateRelationships() {
        int relationCount = 0;
        if (assignment != null) relationCount++;
        if (strengthResults != null) relationCount++;
        if (sessionInsights != null) relationCount++;

        if (relationCount > 1) {
            throw new IllegalStateException("Upload can only belong to one context: Assignment, StrengthResults, or SessionInsights");
        }
    }

}
