package nl.novi.bloomtrail.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.Date;

public class CoachingProgramInputDto {

    private Long coachingProgramId;
    @NotBlank
    @Size(max = 255)
    private String coachingProgramName;

    @Size(max = 500)
    private String goal;

    @NotNull
    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    private Date startDate;

    @NotNull
    @JsonFormat(pattern= "dd-mm-yyy")
    @Future
    private Date endDate;

    private String clientUsername;
    private String coachUsername;


    public Long getCoachingProgramId() {
        return coachingProgramId;
    }

    public void setCoachingProgramId(Long coachingProgramId) {
        this.coachingProgramId = coachingProgramId;
    }

    public String getCoachingProgramName() {
        return coachingProgramName;
    }

    public void setCoachingProgramName(String coachingProgramName) {
        this.coachingProgramName = coachingProgramName;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
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

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public String getCoachUsername() {
        return coachUsername;
    }

    public void setCoachUsername(String coachUsername) {
        this.coachUsername = coachUsername;
    }
}
