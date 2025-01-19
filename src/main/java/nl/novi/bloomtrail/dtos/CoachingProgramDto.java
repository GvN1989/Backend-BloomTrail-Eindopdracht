package nl.novi.bloomtrail.dtos;

import java.util.Date;
import java.util.List;

public class CoachingProgramDto {

    private Long coachingProgramId;

    private String coachingProgramName;
    private String goal;

    private Date startDate;

    private Date endDate;

    private List<String> strengthResultUrls;

    private List<StepDto> timeline;

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
}
