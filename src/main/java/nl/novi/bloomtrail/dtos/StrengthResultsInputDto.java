package nl.novi.bloomtrail.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.Size;

import java.util.List;

public class StrengthResultsInputDto {
    private String filename;
    @Size(max = 500)
    private String summary;
    @ElementCollection
    private List<String> topStrengthNames;

    private Long coachingProgramId;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getTopStrengthNames() {
        return topStrengthNames;
    }

    public void setTopStrengthNames(List<String> topStrengthNames) {
        this.topStrengthNames = topStrengthNames;
    }

    public Long getCoachingProgramId() {
        return coachingProgramId;
    }

    public void setCoachingProgramId(Long coachingProgramId) {
        this.coachingProgramId = coachingProgramId;
    }
}
