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
    private Long strengthResultsId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Size(max = 500)
    private String summary;

    @OneToMany(mappedBy = "strengthResults", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upload> uploads = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "strength_results_top_strengths",
            joinColumns = @JoinColumn(name = "strength_results_id"),
            inverseJoinColumns = @JoinColumn(name = "managing_strength_id")
    )
    private List<ManagingStrength> topStrengths = new ArrayList<>();

    public Long getStrengthResultsId() {
        return strengthResultsId;
    }

    public void setStrengthResultsId(Long strengthResultsId) {
        this.strengthResultsId = strengthResultsId;
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

    public List<Upload> getUploads() {
        return uploads;
    }

    public void setUploads(List<Upload> uploads) {
        this.uploads = uploads;
    }

    public List<ManagingStrength> getTopStrengths() {
        return topStrengths;
    }

    public void setTopStrengths(List<ManagingStrength> topStrengths) {
        this.topStrengths = topStrengths;
    }
}
