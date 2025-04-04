package nl.novi.bloomtrail.models;

import jakarta.persistence.*;

@Entity
@Table(name = "managingStrength")
public class ManagingStrength {

    @Id
    @Column(name = "strength_id")
    private Long strengthId;
    @Column(name = "strength_rank")
    private Integer rank;
    private String strengthNl;
    private String strengthEn;
    private String contributionDoing;
    private String contributionBeing;
    private String needTask;
    private String needRelationship;
    private String externalTriggers;
    private String internalTriggers;
    private String operatingBelief;
    private String misManaged;

    @ManyToOne
    @JoinColumn(name = "strength_results_id", nullable = false)
    private StrengthResults strengthResults;

    public Long getStrengthId() {
        return strengthId;
    }

    public void setStrengthId(Long strengthId) {
        this.strengthId = strengthId;
    }

    public String getStrengthNl() {
        return strengthNl;
    }

    public void setStrengthNl(String strengthNl) {
        this.strengthNl = strengthNl;
    }

    public String getStrengthEn() {
        return strengthEn;
    }

    public void setStrengthEn(String strengthEng) {
        this.strengthEn = strengthEng;
    }

    public String getContributionDoing() {
        return contributionDoing;
    }

    public void setContributionDoing(String contributionDoing) {
        this.contributionDoing = contributionDoing;
    }

    public String getContributionBeing() {
        return contributionBeing;
    }

    public void setContributionBeing(String contributionBeing) {
        this.contributionBeing = contributionBeing;
    }

    public String getNeedTask() {
        return needTask;
    }

    public void setNeedTask(String needTask) {
        this.needTask = needTask;
    }

    public String getNeedRelationship() {
        return needRelationship;
    }

    public void setNeedRelationship(String needRelationship) {
        this.needRelationship = needRelationship;
    }

    public String getExternalTriggers() {
        return externalTriggers;
    }

    public void setExternalTriggers(String externalTriggers) {
        this.externalTriggers = externalTriggers;
    }

    public String getInternalTriggers() {
        return internalTriggers;
    }

    public void setInternalTriggers(String internalTriggers) {
        this.internalTriggers = internalTriggers;
    }

    public String getOperatingBelief() {
        return operatingBelief;
    }

    public void setOperatingBelief(String operatingBelief) {
        this.operatingBelief = operatingBelief;
    }

    public String getMisManaged() {
        return misManaged;
    }

    public void setMisManaged(String misManaged) {
        this.misManaged = misManaged;
    }

    public StrengthResults getStrengthResults() {
        return strengthResults;
    }

    public void setStrengthResults(StrengthResults strengthResults) {
        this.strengthResults = strengthResults;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
