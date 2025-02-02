package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nl.novi.bloomtrail.enums.FileContext;

import java.util.List;

public class SessionInsightInputDto {

    @NotNull(message = "Author cannot be null")
    private String author;
    private String description;
    @NotNull(message = "Indicate if it concerns client Insights or coach notes, this cannot be null")
    private FileContext fileContext;
    @NotNull(message = "SessionId is required, session insight must be linked to a session")
    private Long sessionId;
    private List<Long> fileIds;

    public boolean isValid() {
        return sessionId != null && fileContext != null &&
                (fileContext == FileContext.SESSION_INSIGHTS_CLIENT_REFLECTION ||
                        fileContext == FileContext.SESSION_INSIGHTS_COACH_NOTES);
    }

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

    public FileContext getFileContext() {
        return fileContext;
    }

    public void setFileContext(FileContext fileContext) {
        this.fileContext = fileContext;
    }

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }
}
