package nl.novi.bloomtrail.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.novi.bloomtrail.enums.FileContext;

import java.util.List;

public class StrengthResultsInputDto {
    private String username;
    @Size(max = 500)
    private String summary;
    @ElementCollection
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
