package nl.novi.bloomtrail.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStorageUtil {

    private static final String UPLOAD_DIR = System.getProperty("UPLOAD_DIR", "src/main/resources/uploads/");


    public static String saveFile(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            if (filename.isEmpty()) {
                throw new IllegalArgumentException("Invalid file name");
            }
            return saveFile(file.getBytes(), filename);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public static String saveFile(byte[] fileData, String fileName) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName);
            String uniqueFileName = fileName;
            int counter = 1;
            while (Files.exists(filePath)) {
                uniqueFileName = appendCounterToFilename(fileName, counter++);
                filePath = uploadDir.resolve(uniqueFileName);
            }

            Files.write(filePath, fileData);
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private static String appendCounterToFilename(String fileName, int counter) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName + "_" + counter;
        }
        return fileName.substring(0, dotIndex) + "_" + counter + fileName.substring(dotIndex);
    }

    public static byte[] readFileFromLocalSystem(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

}
