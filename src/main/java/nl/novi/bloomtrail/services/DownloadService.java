package nl.novi.bloomtrail.services;


import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.utils.FileStorageUtil;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class DownloadService {

    private final FileService fileService;

    public DownloadService(FileService fileService) {
        this.fileService = fileService;
    }

    public byte[] downloadFile(String filePath) {
        try {
            return FileStorageUtil.readFileFromLocalSystem(filePath);
        } catch (RuntimeException e) {
            throw new NotFoundException("The requested file could not be found at: " + filePath);
        }
    }

    public byte[] downloadFilesForEntity(Object parentEntity) throws IOException {
        List<File> files = fileService.getUploadsForParentEntity(parentEntity);

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files available for download for the given entity.");
        }

        return createZipFromFiles(files);
    }

    public byte[] createZipFromFiles(List<File> files) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (File file : files) {
                byte[] fileData = fileService.readFileFromStorage(file.getUrl());
                String fileName= "session_" + file.getFileId() + "_" +
                        (file.getOriginalFilename() != null ? file.getOriginalFilename() : "file_" + file.getFileId());

                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                zos.write(fileData);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        }

    }
}


