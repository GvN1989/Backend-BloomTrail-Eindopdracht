package nl.novi.bloomtrail.utils;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationUtils {

    public static void validateEntityExists(boolean exists, String entityName, Object identifier) {
        if (!exists) {
            throw new IllegalArgumentException(entityName + " with identifier " + identifier + " does not exist.");
        }
    }

    public static void validateStatusTransition(String currentStatus, String newStatus, List<String> allowedTransitions) {
        if (!allowedTransitions.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    public static void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
    }

    public static void validateUniqueField(boolean isUnique, String fieldName, Object value) {
        if (!isUnique) {
            throw new IllegalArgumentException("The " + fieldName + " with value " + value + " already exists.");
        }
    }

}
