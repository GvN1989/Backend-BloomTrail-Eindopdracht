package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.mappers.AssignmentMapper;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final FileService fileService;
    private final EntityValidationHelper validationHelper;
    private final DownloadService downloadService;

    public AssignmentService(AssignmentRepository assignmentRepository, FileService fileService, EntityValidationHelper validationHelper, DownloadService downloadService) {
        this.assignmentRepository = assignmentRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public List<Assignment> getAssignmentsByStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);
        return step.getAssignment();
    }

    public List<Assignment> getAssignmentsBySession(Long sessionId) {
        Session session = validationHelper.validateSession(sessionId);
        return session.getAssignment();
    }

    public void uploadFileForAssignment(MultipartFile file, Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        fileService.saveFile(file, FileContext.ASSIGNMENT, assignment);
    }

    public List<File> getUploadsForAssignment(Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        return fileService.getUploadsForParentEntity(assignment);
    }

    public Assignment createAssignment(AssignmentInputDto dto, MultipartFile file) {
        Session session = validationHelper.validateSession(dto.getSessionId());
        Assignment assignment = AssignmentMapper.toAssignmentEntity(dto, session);

        if (file != null && !file.isEmpty()) {
            fileService.saveFile(file, FileContext.ASSIGNMENT, assignment);
        }

        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        fileService.deleteFilesForParentEntity(assignment);
        assignmentRepository.delete(assignment);
    }

    public byte[] downloadAssignmentFiles(Long assignmentId) throws IOException {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        return downloadService.downloadFilesForEntity(assignment);
    }
}
