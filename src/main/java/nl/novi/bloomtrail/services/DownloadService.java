package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.models.CoachingProgram;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        public byte[] downloadFile(String fileUrl) {
            return fileService.downloadFile(fileUrl);
        }

        public byte[] downloadFilesForParentEntity(Object parentEntity) {
            List<File> files = fileService.getUploadsForParentEntity(parentEntity);

            if (files.isEmpty()) {
                throw new IllegalArgumentException("No files available for the given entity.");
            }

            return files.stream()
                    .findFirst()
                    .map(file -> fileService.downloadFile(file.getUrl()))
                    .orElseThrow(() -> new IllegalArgumentException("No valid file URLs found."));
    }

        public List<String> getFilesByStep(Long stepId) {
            Step step = validationHelper.validateStep(stepId);
            List<String> fileUrls = new ArrayList<>();

            step.getSession().forEach(session ->
                    session.getSessionInsights().forEach(insight ->
                            fileUrls.addAll(fileService.getUploadsForParentEntity(insight).stream()
                                    .map(File::getUrl)
                                    .toList())
                    )
            );

            return fileUrls;
        }

        public List<String> getFilesBySession(Long sessionId) {
            Session session = validationHelper.validateSession(sessionId);
            List<String> fileUrls = new ArrayList<>();

            session.getSessionInsights().forEach(insight ->
                    fileUrls.addAll(fileService.getUploadsForParentEntity(insight).stream()
                            .map(File::getUrl)
                            .toList())
            );
            session.getAssignments().forEach(assignment ->
                    fileUrls.addAll(fileService.getUploadsForParentEntity(assignment).stream()
                            .map(File::getUrl)
                            .toList())
            );

            return fileUrls;
        }

    public List<String> getAllFilesByStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);
        return step.getSession().stream()
                .flatMap(session -> getFilesBySession(session.getSessionId()).stream())
                .toList();
    }

        public List<String> getAllFilesByContext(Long coachingProgramId, FileContext context) {
            CoachingProgram coachingProgram = validationHelper.validateCoachingProgram(coachingProgramId);
            List<String> fileUrls = new ArrayList<>();

            switch (context) {
                case SESSION_INSIGHTS_CLIENT_REFLECTION, SESSION_INSIGHTS_COACH_NOTES -> {
                    coachingProgram.getTimeline().forEach(step ->
                            step.getSession().forEach(session ->
                                    session.getSessionInsights().forEach(insight ->
                                            fileUrls.addAll(fileService.getUploadsForParentEntity(insight).stream()
                                                    .filter(file -> file.getContext() == context)
                                                    .map(File::getUrl)
                                                    .toList())
                                    )
                            )
                    );
                }
                case ASSIGNMENT -> {
                    coachingProgram.getTimeline().forEach(step ->
                            step.getSession().forEach(session ->
                                    session.getAssignments().forEach(assignment ->
                                            fileUrls.addAll(fileService.getUploadsForParentEntity(assignment).stream()
                                                    .map(File::getUrl)
                                                    .toList())
                                    )
                            )
                    );
                }
                case STRENGTH_RESULTS -> {
                    coachingProgram.getStrengthResults().forEach(strengthResult ->
                            fileUrls.addAll(fileService.getUploadsForParentEntity(strengthResult).stream()
                                    .map(File::getUrl)
                                    .toList())
                    );
                }
                default -> throw new IllegalArgumentException("Unsupported FileContext: " + context);
            }

            return fileUrls;
        }

    private byte[] createZipFromFiles(List<String> fileUrls) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String fileUrl : fileUrls) {
                byte[] fileData = fileService.downloadFile(fileUrl);
                ZipEntry entry = new ZipEntry(Paths.get(fileUrl).getFileName().toString());
                zos.putNextEntry(entry);
                zos.write(fileData);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }



        public byte[] downloadFilesAsZip(Long coachingProgramId, FileContext context) throws IOException {
            List<String> fileUrls = getAllFilesByContext(coachingProgramId, context);
            return createZipFromFiles(fileUrls);
        }

    public byte[] downloadFilesAsZipForStep(Long stepId) throws IOException {
        List<String> fileUrls = getFilesByStep(stepId);
        return createZipFromFiles(fileUrls);
    }

    public byte[] downloadFilesAsZipForSession(Long sessionId) throws IOException {
        List<String> fileUrls = getFilesBySession(sessionId);
        return createZipFromFiles(fileUrls);
        }


}


