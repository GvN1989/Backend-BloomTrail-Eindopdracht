package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.AssignmentDto;
import nl.novi.bloomtrail.dtos.AssignmentInputDto;
import nl.novi.bloomtrail.models.Assignment;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.Session;

import java.util.stream.Collectors;

public class AssignmentMapper {

    public static AssignmentDto toAssignmentDto (Assignment assignment) {
        AssignmentDto dto= new AssignmentDto();

        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setDescription(assignment.getDescription());
        dto.setFileStatus(assignment.getFileStatus());
        dto.setSessionId(assignment.getSession() != null ? assignment.getSession().getSessionId() : null);
        dto.setDownloadUrl(assignment.getDownloadUrl());

        if(assignment.getFiles() != null && !assignment.getFiles().isEmpty()) {
            dto.setUploadsIds(
                    assignment.getFiles().stream()
                            .map(File::getUploadId)
                            .collect(Collectors.toList())
            );
        }

        return dto;

    }

    public static Assignment toAssignmentEntity(AssignmentInputDto inputDto, Session session) {
        Assignment assignment = new Assignment();

        assignment.setSession(session);
        assignment.setDescription(inputDto.getDescription());
        assignment.setFileStatus(inputDto.getFileStatus());

        return assignment;

    }


}
