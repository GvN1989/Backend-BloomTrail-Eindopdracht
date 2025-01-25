package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.novi.bloomtrail.enums.FileStatus;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;

public class AssignmentInputDto {

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "FileStatus is required")
    private FileStatus fileStatus;

    @NotNull(message = "SessionId is required")
    private Long sessionId;

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

}
