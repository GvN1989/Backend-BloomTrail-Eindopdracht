package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.mappers.AssignmentMapper;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final FileService fileService;
    private final EntityValidationHelper validationHelper;


    public AssignmentService(AssignmentRepository assignmentRepository, FileService fileService, EntityValidationHelper entityValidationHelper, EntityValidationHelper validationHelper) {
        this.assignmentRepository = assignmentRepository;
        this.fileService = fileService;
        this.validationHelper = validationHelper;
    }

    public void uploadFileForAssignment(MultipartFile file, Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        fileService.saveFile(file, FileContext.ASSIGNMENT, assignment);
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getUploadsForAssignment(Long assignmentId) {
        Assignment assignment = validationHelper.validateAssignment(assignmentId);
        return fileService.getUploadsForParentEntity(assignment);
    }

    public Assignment createAssignment(AssignmentInputDto dto) {
        Session session = validationHelper.validateSession(dto.getSessionId());

        Assignment assignment = AssignmentMapper.toAssignmentEntity(dto, session);

        return assignmentRepository.save(assignment);
    }

}
