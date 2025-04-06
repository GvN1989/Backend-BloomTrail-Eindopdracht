package nl.novi.bloomtrail.helper;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {
    public static Map<String, Object> build(int statusCode, String message) {
        HttpStatus status = HttpStatus.valueOf(statusCode);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", statusCode);
        error.put("error", status.getReasonPhrase());
        error.put("message", message);

        return error;
    }

}
