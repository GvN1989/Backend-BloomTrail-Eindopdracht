package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name= "Strength_Programs")
public class StrengthProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer strengthProgrammeId;
    private String goal;
    private Date startDate;
    private Date endDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "strength_results_id")
    private StrengthResults strengthResults;

    @OneToMany(mappedBy = "strengthProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> timeline = new ArrayList<>();

    private String managingStrengthResultDownload;

    public Integer getStrengthProgrammeId() {
        return strengthProgrammeId;
    }

    public void setStrengthProgrammeId(Integer strengthProgrammeId) {
        this.strengthProgrammeId = strengthProgrammeId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
    public List<Step> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<Step> timeline) {
        this.timeline = timeline;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public StrengthResults getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(StrengthResults strengthResults) {
        this.strengthResults = strengthResults;
    }

    public String getManagingStrengthResultDownload() {
        return managingStrengthResultDownload;
    }

    public void setManagingStrengthResultDownload(String managingStrengthResultDownload) {
        this.managingStrengthResultDownload = managingStrengthResultDownload;
    }

}
