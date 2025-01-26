package nl.novi.bloomtrail.dtos;

import java.util.Date;

public class CoachingProgramRoleDto {

    private Long coachingProgramId;
    private String coachingProgramName;
    private String goal;
    private Date startDate;
    private Date endDate;
    private String role;

    public CoachingProgramRoleDto(Long coachingProgramId, String coachingProgramName, String goal, Date startDate, Date endDate, String role) {
        this.coachingProgramId = coachingProgramId;
        this.coachingProgramName = coachingProgramName;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.role = role;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
