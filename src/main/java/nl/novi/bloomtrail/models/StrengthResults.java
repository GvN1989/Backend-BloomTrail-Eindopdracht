package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "strength_results")
public class StrengthResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "strength_results_id")
    private Long resultsId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Size(max = 500)
    private String summary;
    @ElementCollection
    private List<String> topStrengthNames;
    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    public Long getResultsId() {
        return resultsId;
    }

    public void setResultsId(Long strengthResultsId) {
        this.resultsId = strengthResultsId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<String> getTopStrengthNames() {
        return topStrengthNames;
    }

    public void setTopStrengthNames(List<String> topStrengthNames) {
        this.topStrengthNames = topStrengthNames;
    }


}
