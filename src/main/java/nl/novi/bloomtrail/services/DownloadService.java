package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class DownloadService {

    private final FileService fileService;
    private final EntityValidationHelper validationHelper;

    public DownloadService(FileService fileService, EntityValidationHelper validationHelper) {
        this.fileService = fileService;
        this.validationHelper = validationHelper;
    }

    public byte[] downloadFile(String url) {
        try {
            return Files.readAllBytes(Paths.get(url));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from local system: " + url, e);
        }
    }
    public byte[] downloadFilesForEntity(Object parentEntity) throws IOException {
        List<File> files = fileService.getUploadsForParentEntity(parentEntity);

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files available for download for the given entity.");
        }

        if (files.size() == 1) {
            return downloadFile(files.get(0).getUrl());
        }

        List<String> fileUrls = files.stream()
                .map(File::getUrl)
                .toList();

        return createZipFromFiles(fileUrls);
    }

    public byte[] downloadSessionInsightFiles(Long sessionInsightId, FileContext context) throws IOException {
        SessionInsight sessionInsight = validationHelper.validateSessionInsight(sessionInsightId);

        List<File> files;

        if (context != null) {
            files = fileService.getUploadsForParentEntity(sessionInsight).stream()
                    .filter(file -> file.getContext() == context)
                    .toList();
        } else {
            files = fileService.getUploadsForParentEntity(sessionInsight);
        }

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No files available for download.");
        }

        if (files.size() == 1) {
            return downloadFile(files.get(0).getUrl());
        }

        List<String> fileUrls = files.stream()
                .map(File::getUrl)
                .toList();

        return createZipFromFiles(fileUrls);
    }

    public byte[] downloadStrengthResults(Long strengthResultsId, boolean includeReport) throws IOException {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(strengthResultsId);

        List<File> files = fileService.getUploadsForParentEntity(strengthResults);
        List<String> fileUrls = files.stream()
                .map(File::getUrl)
                .toList();

        if (includeReport && strengthResults.getStrengthResultsFilePath() != null) {
            fileUrls.add(strengthResults.getStrengthResultsFilePath());
        }

        if (fileUrls.isEmpty()) {
            throw new IllegalArgumentException("No files available for download for the given StrengthResults ID: " + strengthResultsId);
        }

        if (fileUrls.size() == 1) {
            return downloadFile(fileUrls.get(0));
        }

        return createZipFromFiles(fileUrls);
    }


    public byte[] createZipFromFiles(List<String> fileUrls) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String fileUrl : fileUrls) {
                byte[] fileData = downloadFile(fileUrl);
                ZipEntry entry = new ZipEntry(Paths.get(fileUrl).getFileName().toString());
                zos.putNextEntry(entry);
                zos.write(fileData);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }


}


