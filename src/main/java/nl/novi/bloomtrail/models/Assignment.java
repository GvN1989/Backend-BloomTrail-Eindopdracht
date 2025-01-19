package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import nl.novi.bloomtrail.common.Downloadable;
import nl.novi.bloomtrail.common.Uploadable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
public class Assignment implements Uploadable, Downloadable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assignmentId;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upload> uploads = new ArrayList<>();

    private String downloadUrl;

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setUploads(List<Upload> uploads) {
        this.uploads = uploads;
    }

    @Override
    public String getDownload() {
        return downloadUrl;
    }

    @Override
    public void setDownload(String download) {
        this.downloadUrl = download;
    }

    @Override
    public List<Upload> getUploads() {
        return uploads;
    }

    @Override
    public void addUpload(Upload upload) {
        uploads.add(upload);
        upload.setAssignment(this);
    }

    @Override
    public void removeUpload(Upload upload) {
        uploads.remove(upload);
        upload.setAssignment(null);
    }

    @Override
    public String generateDownloadUrl() {
        return uploads.stream()
                .map(Upload::getUrl)
                .findFirst()
                .orElse("No uploads available");
    }
}
