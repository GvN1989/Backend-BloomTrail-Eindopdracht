package nl.novi.bloomtrail.dtos;

public class StepReorderDto {
    private Long stepId;
    private Integer newSequence;

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public Integer getNewSequence() {
        return newSequence;
    }

    public void setNewSequence(Integer newSequence) {
        this.newSequence = newSequence;
    }
}
