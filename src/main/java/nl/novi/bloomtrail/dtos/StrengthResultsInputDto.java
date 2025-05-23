package nl.novi.bloomtrail.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class StrengthResultsInputDto {
    private String username;
    @Size(max = 500)
    private String summary;
    @ElementCollection
    @NotNull(message = "Top strengths must be provided")
    @Size(min = 5, max = 15, message = "You must provide between 5 and 15 strengths")
    private List<String> topStrengthNames;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


}
