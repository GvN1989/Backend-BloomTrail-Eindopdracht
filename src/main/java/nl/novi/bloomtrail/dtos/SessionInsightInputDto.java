package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nl.novi.bloomtrail.enums.FileContext;

import java.util.List;

public class SessionInsightInputDto {

    @NotNull(message = "Author cannot be null")
    private String author;
    private String description;
    @NotNull(message = "SessionId is required, session insight must be linked to a session")
    private Long sessionId;
    private List<Long> fileIds;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }


    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }
}
