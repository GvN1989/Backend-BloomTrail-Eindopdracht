package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.File;
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
    private final ValidationHelper validationHelper;
    private final DownloadService downloadService;

    public AssignmentService(AssignmentRepository assignmentRepository, FileService fileService, ValidationHelper validationHelper, DownloadService downloadService) {
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

    public Assignment updateAssignment(Long assignmentId, AssignmentInputDto inputDto) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);

        if (inputDto.getStepId() != null) {
            Step step = validationHelper.validateStep(inputDto.getStepId());
            assignment.setStep(step);
        }

        if (inputDto.getSessionId() != null) {
            Session session = validationHelper.validateSession(inputDto.getSessionId());
            assignment.setSession(session);
        }

        return assignmentRepository.save(assignment);
    }

    public Assignment createAssignment(AssignmentInputDto inputDto, MultipartFile file) {
        if (!inputDto.isValid()) {
            throw new IllegalArgumentException("Assignment must be linked to a step.");
        }

        Assignment assignment = new Assignment();
        assignment.setDescription(inputDto.getDescription());

        Step step = validationHelper.validateStep(inputDto.getStepId());
        assignment.setStep(step);

        if (inputDto.getSessionId() != null) {
            Session session = validationHelper.validateSession(inputDto.getSessionId());
            assignment.setSession(session);
        }

        Assignment savedAssignment = assignmentRepository.save(assignment);

        if (file != null && !file.isEmpty()) {
            fileService.saveFile(file, FileContext.ASSIGNMENT, savedAssignment);
        }

        return savedAssignment;
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
