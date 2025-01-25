package nl.novi.bloomtrail.dtos;

import java.util.List;

public class AssignmentDto {

    private Long assignmentId;
    private String description;
    private String fileStatus;
    private Long sessionId;
    private List<Long> uploadsIds;;


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

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
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

}
