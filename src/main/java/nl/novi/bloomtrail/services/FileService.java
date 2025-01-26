package nl.novi.bloomtrail.services;

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


    public FileService(FileRepository fileRepository) {
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

        switch (parentEntity) {
            case Assignment assignment -> file.setAssignment(assignment);
            case StrengthResults strengthResults -> file.setStrengthResults(strengthResults);
            case SessionInsight sessionInsight -> file.setSessionInsights(sessionInsight);
            case User user when context == FileContext.PROFILE_PICTURE -> file.setUser(user);
            case null, default -> throw new IllegalArgumentException("Unsupported parent entity type or context");
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
            case SessionInsight sessionInsight -> fileRepository.findBySessionInsights(sessionInsight);
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

        List<File> files = getUploadsForParentEntity(parentEntity);

        files.forEach(file -> {
            Path filePath = Paths.get(file.getUrl());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + file.getUrl(), e);
            }
        });

        fileRepository.deleteAll(files);
    }

}
