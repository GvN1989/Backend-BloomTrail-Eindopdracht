package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.AssignmentDto;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.exceptions.MappingException;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;


import java.util.stream.Collectors;

public class AssignmentMapper {

    public static AssignmentDto toAssignmentDto(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();

        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setDescription(assignment.getDescription());
        dto.setSessionId(assignment.getSession() != null ? assignment.getSession().getSessionId() : null);
        dto.setStepId(assignment.getStep() != null ? assignment.getStep().getStepId() : null);
        if (assignment.getFiles() != null && !assignment.getFiles().isEmpty()) {
            dto.setUploadsIds(
                    assignment.getFiles().stream()
                            .map(File::getFileId)
                            .collect(Collectors.toList())
            );
        }

        return dto;

    }

    public static Assignment toAssignmentEntity(AssignmentInputDto inputDto, Session session, Step step) {
        if (inputDto == null) {
            throw new MappingException("AssignmentInputDto cannot be null");
        }

        try {
            Assignment assignment = new Assignment();

            assignment.setDescription(inputDto.getDescription());
            if (session != null) {
                assignment.setSession(session);
            }
            if (step != null) {
                assignment.setStep(step);
            }

            return assignment;

        } catch (Exception e) {
            throw new MappingException("Error mapping AssignmentInputDto to Assignment", e);
        }
    }
}
