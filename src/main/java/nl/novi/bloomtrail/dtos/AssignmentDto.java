package nl.novi.bloomtrail.dtos;

import nl.novi.bloomtrail.enums.FileStatus;

import java.util.List;

public class AssignmentDto {

    private Long assignmentId;
    private String description;
    private FileStatus fileStatus;
    private Long sessionId;
    private List<Long> uploadsIds;;
    private String downloadUrl;


    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getUploadsIds() {
        return uploadsIds;
    }

    public void setUploadsIds(List<Long> uploadsIds) {
        this.uploadsIds = uploadsIds;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
