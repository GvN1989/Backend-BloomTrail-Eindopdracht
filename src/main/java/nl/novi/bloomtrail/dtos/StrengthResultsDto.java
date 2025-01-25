package nl.novi.bloomtrail.dtos;

import java.util.Date;
import java.util.List;

public class StrengthResultsDto {

    private Long resultsId;
    private String filename;
    private Date createdAt;
    private Date updatedAt;
    private String summary;
    private String strengthResultsFilePath;
    private List<String> topStrengthNames;
    private List<String> fileUrls;;
    private Long coachingProgramId;
    private String coachingProgramName;
    private String downloadUrl;

    public Long getResultsId() {
        return resultsId;
    }

    public void setResultsId(Long resultsId) {
        this.resultsId = resultsId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getStrengthResultsFilePath() {
        return strengthResultsFilePath;
    }

    public void setStrengthResultsFilePath(String strengthResultsFilePath) {
        this.strengthResultsFilePath = strengthResultsFilePath;
    }

    public List<String> getTopStrengthNames() {
        return topStrengthNames;
    }

    public void setTopStrengthNames(List<String> topStrengthNames) {
        this.topStrengthNames = topStrengthNames;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public String getCoachingProgramName() {
        return coachingProgramName;
    }

    public void setCoachingProgramName(String coachingProgramName) {
        this.coachingProgramName = coachingProgramName;
    }

    public Long getCoachingProgramId() {
        return coachingProgramId;
    }

    public void setCoachingProgramId(Long coachingProgramId) {
        this.coachingProgramId = coachingProgramId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
