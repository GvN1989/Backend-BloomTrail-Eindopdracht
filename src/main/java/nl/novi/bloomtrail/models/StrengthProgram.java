package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name= "StrengthPrograms")
public class StrengthProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer strengthProgrammeId;
    private String goal;
    private List <Step> timeline;
    private Date startDate;
    private Date endDate;
    private String strengthsResultsUpload;
    private String managingStrengthResultDownload;


    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setTimeline(List<Step> timeline) {
        this.timeline = timeline;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStrengthsResultsUpload(String strengthsResultsUpload) {
        this.strengthsResultsUpload = strengthsResultsUpload;
    }

    public void setManagingStrengthResultDownload(String managingStrengthResultDownload) {
        this.managingStrengthResultDownload = managingStrengthResultDownload;
    }
}
