package nl.novi.bloomtrail.dtos;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class CoachingProgramDto {

    private Long coachingProgramId;
    private String coachingProgramName;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> strengthResultUrls;
    private List<StepDto> timeline;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<String> getStrengthResultUrls() {
        return strengthResultUrls;
    }

    public void setStrengthResultUrls(List<String> strengthResultUrls) {
        this.strengthResultUrls = strengthResultUrls;
    }

    public List<StepDto> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<StepDto> timeline) {
        this.timeline = timeline;
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
