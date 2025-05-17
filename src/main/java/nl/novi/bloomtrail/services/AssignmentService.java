package nl.novi.bloomtrail.services;

import jakarta.persistence.EntityNotFoundException;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.mappers.AssignmentMapper;
import nl.novi.bloomtrail.models.Assignment;
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
    private final AccessValidator accessValidator;
    private final ValidationHelper validationHelper;
    private final DownloadService downloadService;

    public AssignmentService(AssignmentRepository assignmentRepository, FileService fileService, AccessValidator accessValidator, ValidationHelper validationHelper, DownloadService downloadService) {
        this.assignmentRepository = assignmentRepository;
        this.fileService = fileService;
        this.accessValidator = accessValidator;
        this.validationHelper = validationHelper;
        this.downloadService = downloadService;
    }

    public List<Assignment> getAssignmentsByStep(Long stepId) {
        Step step = validationHelper.validateStep(stepId);
        accessValidator.validateClientOrCoachOrAdminAccess(step.getCoachingProgram());

        return step.getAssignments();
    }

    public Assignment createAssignment(AssignmentInputDto inputDto, MultipartFile[] files) {

        Step step = validationHelper.validateStep(inputDto.getStepId());
        accessValidator.validateCoachOwnsStepOrAdmin(step);

        Assignment assignment = AssignmentMapper.toAssignmentEntity(inputDto,step);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        assignmentRepository.flush();

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    fileService.saveFile(file, FileContext.ASSIGNMENT, savedAssignment);
                }
            }
        }

        return assignmentRepository.findByAssignmentId(savedAssignment.getAssignmentId())
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found after creation"));
    }

    public Assignment updateAssignment(Long assignmentId, AssignmentInputDto inputDto, MultipartFile[] files) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);

        Step currentStep = assignment.getStep();
        Step newStep = inputDto.getStepId() != null
                ? validationHelper.validateStep(inputDto.getStepId())
                : assignment.getStep();

        if (!currentStep.equals(newStep)) {
            accessValidator.validateCoachOwnsStepOrAdmin(currentStep);
            accessValidator.validateCoachOwnsStepOrAdmin(newStep);
        } else {
            accessValidator.validateCoachOwnsStepOrAdmin(currentStep);
        }

        AssignmentMapper.updateAssignmentFromDto(assignment, inputDto, newStep);

        if (files != null && files.length > 0) {
            fileService.deleteFilesForParentEntity(assignment);

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    fileService.saveFile(file, FileContext.ASSIGNMENT, assignment);
                }
            }
        }

        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        accessValidator.validateCoachOwnsProgramOrIsAdmin(assignment.getStep().getCoachingProgram());
        fileService.deleteFilesForParentEntity(assignment);
        assignmentRepository.delete(assignment);
    }

    public byte[] downloadAssignmentFiles(Long assignmentId) throws IOException {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        accessValidator.validateClientOrCoachOrAdminAccess(assignment.getStep().getCoachingProgram());

        List<File> files = fileService.getUploadsForParentEntity(assignment);

        if (files.isEmpty()) {
            throw new NotFoundException("No files found for assignment " + assignmentId);
        }

        return downloadService.downloadFilesForEntity(assignment);
    }
}
