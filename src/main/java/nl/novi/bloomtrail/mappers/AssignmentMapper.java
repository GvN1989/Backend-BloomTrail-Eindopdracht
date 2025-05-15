package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.AssignmentDto;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.dtos.SessionInputDto;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;

public class AssignmentMapper {

    public static AssignmentDto toAssignmentDto(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();

        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setDescription(assignment.getDescription());
        dto.setStepId(assignment.getStep() != null ? assignment.getStep().getStepId() : null);
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        if (assignment.getFiles() != null && !assignment.getFiles().isEmpty()) {
            dto.setFileUrls(
                    assignment.getFiles().stream()
                            .map(file -> "/files/"+ file.getFileId())
                            .toList()
            );
        } else {
            dto.setFileUrls(Collections.emptyList());
        }
        return dto;

    }

    public static Assignment toAssignmentEntity(AssignmentInputDto inputDto, Step step) {
        if (inputDto == null) {
            throw new ForbiddenException("AssignmentInputDto cannot be null");
        }

        Assignment assignment = new Assignment();
        assignment.setDescription(inputDto.getDescription());
        assignment.setStep(step);

        return assignment;
    }

    public static void updateAssignmentFromDto(Assignment assignment, AssignmentInputDto inputDto, Step newStep) {
        if (inputDto.getDescription() != null) {
            assignment.setDescription(inputDto.getDescription());
        }

        if (newStep != null && !newStep.getStepId().equals(assignment.getStep().getStepId())) {
            assignment.setStep(newStep);
        }
    }

}
