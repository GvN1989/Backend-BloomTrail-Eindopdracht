package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.NotBlank;
import nl.novi.bloomtrail.enums.FileContext;

import java.util.List;

public class SessionInsightInputDto {

    @NotBlank
    private String author;
    private String description;
    private FileContext fileContext;
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
