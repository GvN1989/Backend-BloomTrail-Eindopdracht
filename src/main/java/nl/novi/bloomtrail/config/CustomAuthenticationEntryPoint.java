package nl.novi.bloomtrail.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.novi.bloomtrail.exceptions.UnauthorizedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @PostConstruct
    public void init() {
        System.out.println("✅ CustomAuthenticationEntryPoint has been initialized!");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setContentType("application/json");

        Map<String, Object> errorDetails = new HashMap<>();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Authentication failed, please login or provide (correct) token.");
        errorDetails.put("status", 401);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}
