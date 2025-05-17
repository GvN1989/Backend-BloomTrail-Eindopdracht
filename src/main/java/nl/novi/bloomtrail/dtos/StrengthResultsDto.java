package nl.novi.bloomtrail.dtos;

import java.util.Date;
import java.util.List;

public class StrengthResultsDto {

    private Long resultsId;
    private String username;
    private Date createdAt;
    private Date updatedAt;
    private String summary;
    private List<String> topStrengthNames;

    public Long getResultsId() {
        return resultsId;
    }

    public void setResultsId(Long resultsId) {
        this.resultsId = resultsId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
