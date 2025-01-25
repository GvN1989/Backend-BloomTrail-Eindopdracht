package nl.novi.bloomtrail.utils;

import nl.novi.bloomtrail.exceptions.MappingException;

import java.util.function.Supplier;

public class MapperUtils {

    public static <T> T validateAndMap(T inputDto, Runnable validationLogic, Supplier<T> mapperLogic) {
        try {
            validationLogic.run();
            return mapperLogic.get();
        } catch (Exception ex) {
            throw new MappingException("Mapping failed: " + ex.getMessage());
        }
    }
}
