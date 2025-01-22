package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.FileDto;
import nl.novi.bloomtrail.dtos.FileInputDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.models.File;

public class FileMapper {

    public static FileDto toFileDto (File file) {
        FileDto dto = new FileDto();

        dto.setFileId(file.getFileId());
        dto.setFileType(file.getFileType());
        dto.setUrl(file.getUrl());
        dto.setContext(file.getContext() !=null ? file.getContext().toString() : null);

        return dto;
    }

    public static File toFileEntity(FileInputDto inputDto){
        File file = new File();

        file.setFileType(inputDto.getFileType());
        file.setUrl(inputDto.getUrl());
        try {
            file.setContext(inputDto.getContext() != null ? FileContext.valueOf(inputDto.getContext()) : null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid FileContext value: " + inputDto.getContext(), e);
        }

        return file;
    }

}
