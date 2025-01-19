package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.*;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.repositories.FileRepository;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.utils.FileStorageUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File saveUpload(MultipartFile file, FileContext context, Object parentEntity) {
        try {
            String fileUrl = FileStorageUtil.saveFileAndGetUrl(file);

            File upload = new File();
            upload.setFileType(file.getContentType());
            upload.setUrl(fileUrl);
            upload.setContext(context);

            switch (parentEntity) {
                case Assignment assignment -> upload.setAssignment(assignment);
                case StrengthResults strengthResults -> upload.setStrengthResults(strengthResults);
                case SessionInsights sessionInsights -> upload.setSessionInsights(sessionInsights);
                default -> throw new IllegalArgumentException("Unsupported upload file type");
            }

            return fileRepository.save(upload);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save upload", e);
        }
    }

    public List<File> getUploadsByContext(FileContext context) {
        return fileRepository.findByContext(context);
    }

    public List<File> getUploadsForParentEntity(Object parentEntity) {

        if (parentEntity == null) {
            throw new IllegalArgumentException("Parent entity cannot be null");
        }

        return switch(parentEntity) {
            case Assignment assignment -> fileRepository.findByAssignment(assignment);
            case StrengthResults strengthResults -> fileRepository.findByStrengthResults(strengthResults);
            case SessionInsights sessionInsights -> fileRepository.findBySessionInsights(sessionInsights);
            default -> throw new IllegalArgumentException("Unsupported file type");
        };
    }

    public byte[] downloadFile(String url) {
        try {
            return Files.readAllBytes(Paths.get(url));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from local system: " + url, e);
        }
    }

    public void deleteUpload(Long uploadId) {
        File file = fileRepository.findById(uploadId)
                .orElseThrow(() -> new RuntimeException("Upload not found"));
        fileRepository.delete(file);
    }

}
