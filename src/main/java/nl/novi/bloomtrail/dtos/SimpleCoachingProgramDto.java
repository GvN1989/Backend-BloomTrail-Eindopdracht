package nl.novi.bloomtrail.dtos;

public class SimpleCoachingProgramDto {
    private Long coachingProgramId;
    private String coachingProgramName;
    private String clientUsername;
    private String coachUsername;
    private int stepCount;
    private double progress;

    public SimpleCoachingProgramDto(Long coachingProgramId, String coachingProgramName, String clientUsername, String coachUsername, int stepCount, double progress) {
        this.coachingProgramId = coachingProgramId;
        this.coachingProgramName = coachingProgramName;
        this.clientUsername = clientUsername;
        this.coachUsername = coachUsername;
        this.stepCount= stepCount;
        this.progress = progress;
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

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
