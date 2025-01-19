package nl.novi.bloomtrail.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class AuthenticationResponse {

    @JsonProperty("token")
    private final String jwt;

    public AuthenticationResponse(String jwt) {
        if (jwt == null || jwt.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }
        this.jwt = jwt;
    }

}
