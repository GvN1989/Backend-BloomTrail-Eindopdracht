package nl.novi.bloomtrail.services;

import jakarta.persistence.EntityNotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.*;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.utils.FileStorageUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository, AssignmentRepository assignmentRepository, SessionInsightRepository sessionInsightRepository, StrengthResultsRepository strengthResultsRepository) {
        this.fileRepository = fileRepository;
    }

    public File saveFile(MultipartFile file, FileContext context, Object parentEntity) {
        return saveFile(FileStorageUtil.saveFile(file), context, parentEntity, file.getContentType());
    }

    public File saveFile(byte[] fileData, String fileName, FileContext context) {
        String fileUrl = FileStorageUtil.saveFile(fileData, fileName);

        File file = new File();
        file.setFileType("application/pdf");
        file.setUrl(fileUrl);
        file.setContext(context);

        return fileRepository.save(file);
    }

    private File saveFile(String url, FileContext context, Object parentEntity, String fileType) {
        File file = new File();
        file.setUrl(url);
        file.setContext(context);
        file.setFileType(fileType);

        if (parentEntity instanceof Assignment assignment) {
            file.setAssignment(assignment);
        } else if (parentEntity instanceof StrengthResults strengthResults) {
            file.setStrengthResults(strengthResults);
        } else if (parentEntity instanceof SessionInsight sessionInsight) {
            file.setSessionInsights(sessionInsight);
        } else if (parentEntity instanceof User user) {
            file.setUser(user);
        } else {
            throw new IllegalArgumentException("Unsupported parent entity type or context");
        }

        return fileRepository.save(file);
    }

    public List<File> getUploadsByContext(FileContext context) {
        return fileRepository.findByContext(context);
    }

    public List<File> getUploadsForParentEntity(Object parentEntity) {

        if (parentEntity == null) {
            throw new IllegalArgumentException("Parent entity cannot be null");
        }

        return switch (parentEntity) {
            case Assignment assignment -> fileRepository.findByAssignment(assignment);
            case StrengthResults strengthResults -> fileRepository.findByStrengthResults(strengthResults);
            case SessionInsight sessionInsight -> fileRepository.findBySessionInsight(sessionInsight);
            default -> throw new IllegalArgumentException("Unsupported file type");
        };
    }
    private byte[] readFileFromStorage(String url) {
        try {
            return Files.readAllBytes(Paths.get(url));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from local system: " + url, e);
        }
    }

    public void deleteFilesForParentEntity(Object parentEntity) {
        if (parentEntity == null) {
            throw new IllegalArgumentException("Parent entity cannot be null");
        }

        if (parentEntity instanceof User user) {
            if (user.getProfilePicture() != null) {
                deleteFile(user.getProfilePicture());
            }
        } else {
            List<File> files = getUploadsForParentEntity(parentEntity);

            files.forEach(this::deleteFile);
        }
    }

    public void deleteFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        Path filePath = Paths.get(file.getUrl());
        try {
            Files.deleteIfExists(filePath);
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + file.getUrl(), e);
        }
    }

}
