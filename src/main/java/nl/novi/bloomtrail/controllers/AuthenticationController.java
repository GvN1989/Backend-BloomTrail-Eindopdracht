package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.AuthenticatedUserDto;
import nl.novi.bloomtrail.dtos.AuthenticationRequest;
import nl.novi.bloomtrail.dtos.AuthenticationResponse;
import nl.novi.bloomtrail.helper.ErrorResponseBuilder;
import nl.novi.bloomtrail.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = "/authenticated")
    public ResponseEntity<Object> authenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponseBuilder.build(401, "User is not authenticated."));
        }
        String username = authentication.getName();
        String authority = authentication.getAuthorities().stream()
                .findFirst()
                .map(granted -> granted.getAuthority())
                .orElse("UNKNOWN");

        return ResponseEntity.ok(new AuthenticatedUserDto(username, authority, true));
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody(required = false) AuthenticationRequest authenticationRequest) {

        if (authenticationRequest == null) {
            return ResponseEntity.badRequest().body(
                    ErrorResponseBuilder.build(400, "The request body is missing. Please provide 'username' and 'password'.")
            );
        }

        if (authenticationRequest.getUsername() == null || authenticationRequest.getUsername().trim().isEmpty()
                || authenticationRequest.getPassword() == null || authenticationRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponseBuilder.build(400, "Username and password must not be null or empty.")
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            final String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(401).body(
                    ErrorResponseBuilder.build(401, "Invalid username or password.")
            );
        }
    }
    @PostMapping("/logout/{username}")
    public ResponseEntity<?> logout(@RequestHeader(value= "Authorization", required = false) String authHeader, Authentication authentication){

        String token = authHeader !=null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : null;

        String username = (authentication != null) ? authentication.getName() : null;

        String message = (username != null)
                ? "User '" + username + "' logged out successfully."
                : "Logout request received.";

        return ResponseEntity.ok(ErrorResponseBuilder.build(200, message));
    }
}
