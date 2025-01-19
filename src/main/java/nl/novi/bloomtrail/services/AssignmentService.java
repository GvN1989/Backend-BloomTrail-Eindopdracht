package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final FileService fileService;


    public AssignmentService(AssignmentRepository assignmentRepository, FileService fileService) {
        this.assignmentRepository = assignmentRepository;
        this.fileService = fileService;
    }

    public void uploadAssignmentFile(MultipartFile file, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        fileService.saveUpload(file, FileContext.ASSIGNMENT, assignment);
    }

    public byte[] downloadFile(String url) {
        return fileService.downloadFile(url);
    }

    public List<File> getUploadsForAssignment(Long assignmentId) {
        Assignment assignments = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        return fileService.getUploadsForParentEntity(assignments);
    }

}
