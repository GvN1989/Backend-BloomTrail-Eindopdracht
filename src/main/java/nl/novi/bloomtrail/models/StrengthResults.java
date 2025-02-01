package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "strength_results")
public class StrengthResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "strength_results_id")
    private Long resultsId;
    private String filename;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Size(max = 500)
    private String summary;
    private String strengthResultsFilePath;
    @ElementCollection
    private List<String> topStrengthNames;
    @OneToMany(mappedBy = "strengthResults", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "coaching_program", insertable = false, updatable = false)
    private CoachingProgram coachingProgram;

    @ManyToMany
    @JoinTable(
            name = "strength_results_managing_strengths",
            joinColumns = @JoinColumn(name = "strength_results_id"),
            inverseJoinColumns = @JoinColumn(name = "managing_strength_id")
    )
    private List<ManagingStrength> managingStrengths = new ArrayList<>();

    public Long getResultsId() {
        return resultsId;
    }

    public void setResultsId(Long strengthResultsId) {
        this.resultsId = strengthResultsId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<File> getUploads() {
        return files;
    }

    public void setUploads(List<File> files) {
        this.files = files;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStrengthResultsFilePath() {
        return strengthResultsFilePath;
    }

    public void setStrengthResultsFilePath(String strengthResultsFilePath) {
        this.strengthResultsFilePath = strengthResultsFilePath;
    }

    public List<String> getTopStrengthNames() {
        return topStrengthNames;
    }

    public void setTopStrengthNames(List<String> topStrengthNames) {
        this.topStrengthNames = topStrengthNames;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public CoachingProgram getCoachingProgram() {
        return coachingProgram;
    }

    public void setCoachingProgram(CoachingProgram coachingProgram) {
        this.coachingProgram = coachingProgram;
    }

    public List<ManagingStrength> getManagingStrengths() {
        return managingStrengths;
    }

    public void setManagingStrengths(List<ManagingStrength> managingStrengths) {
        this.managingStrengths = managingStrengths;
    }
}
