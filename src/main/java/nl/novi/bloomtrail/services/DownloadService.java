package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class DownloadService {

    private final FileService fileService;
    private final ValidationHelper validationHelper;

    public DownloadService(FileService fileService, ValidationHelper validationHelper) {
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

        List<byte[]> fileContents = files.stream()
                .map(file -> {
                    try {
                        return downloadFile(file.getUrl());
                    } catch (Exception e) {
                        throw new RuntimeException("Error reading file: " + file.getUrl(), e);
                    }
                })
                .toList();

        return createZipFromFiles(fileContents);
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

        List<byte[]> fileContents = files.stream()
                .map(file -> {
                    try {
                        return downloadFile(file.getUrl());
                    } catch (Exception e) {
                        throw new RuntimeException("Error reading file: " + file.getUrl(), e);
                    }
                })
                .toList();

        return createZipFromFiles(fileContents);
    }

    public byte[] downloadStrengthResults(Long resultsId, boolean includeReport) throws IOException {
        StrengthResults strengthResults = validationHelper.validateStrengthResult(resultsId);

        List<File> files = fileService.getUploadsForParentEntity(strengthResults);
        List<String> fileUrls = new ArrayList<>(files.stream()
                .map(File::getUrl)
                .toList());

        if (includeReport && strengthResults.getStrengthResultsFilePath() != null) {
            fileUrls.add(strengthResults.getStrengthResultsFilePath());
        }

        if (fileUrls.isEmpty()) {
            throw new IllegalArgumentException("No files available for download for the given StrengthResults ID: " + resultsId);
        }

        if (fileUrls.size() == 1) {
            return downloadFile(fileUrls.get(0));
        }

        List<byte[]> fileContents = fileUrls.stream()
                .map(filePath -> {
                    try {
                        return downloadFile(filePath);
                    } catch (Exception e) {
                        throw new RuntimeException("Error reading file: " + filePath, e);
                    }
                })
                .toList();

        return createZipFromFiles(fileContents);
    }


    public byte[] createZipFromFiles(List<byte[]> filesData) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            int index = 1;
            for (byte[] fileData : filesData) {
                ZipEntry zipEntry = new ZipEntry("report_" + index++ + ".pdf");
                zos.putNextEntry(zipEntry);
                zos.write(fileData);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        }

    }
}


