package nl.novi.bloomtrail.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressId;
    private Double percentageComplete;
    private String Remarks;
    private Date lastProgressUpdate;

    public void setProgressId(Integer progressId) {
        this.progressId = progressId;
    }

    public void setPercentageComplete(Double percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void setLastProgressUpdate(Date lastProgressUpdate) {
        this.lastProgressUpdate = lastProgressUpdate;
    }
}
