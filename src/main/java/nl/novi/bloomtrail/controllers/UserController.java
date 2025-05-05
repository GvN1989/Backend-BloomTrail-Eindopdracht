package nl.novi.bloomtrail.controllers;

import jakarta.validation.Valid;
import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.services.UserService;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final AccessValidator accessValidator;

    public UserController(UserService userService, AccessValidator accessValidator) {
        this.userService = userService;
        this.accessValidator = accessValidator;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userProfiles = userService.getUsers();
        return ResponseEntity.ok().body(userProfiles);
    }
    @GetMapping(value = "/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        UserDto userDto = userService.getUser(username);
        return ResponseEntity.ok(userDto);
    }
    @PostMapping(value = "/")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserInputDto dto) throws BadRequestException {

        String newUsername = userService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUsername)
                .toUri();

        UserDto createdUser = userService.getUser(newUsername);

        return ResponseEntity.created(location).body(createdUser);
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(value = "/{username}/authority")
    public ResponseEntity<Object> getUserAuthorities(@PathVariable("username") String username) {
        Authority authority = userService.getAuthority(username);

        if (authority == null) {
            throw new NotFoundException("User " + username + " has no assigned role.");
        }

        return ResponseEntity.ok(authority);
    }
    @PutMapping(value = "/{username}/authority")
    public ResponseEntity<Object> updateUserAuthority(@PathVariable("username") String username, @RequestBody Map<String, Object> fields) {
        if (!(fields.get("authority") instanceof String authorityName) || authorityName.trim().isEmpty()) {
            throw new BadRequestException("Authority cannot be empty or missing.");
        }

        accessValidator.validateAuthority(authorityName);
        UserDto updatedUser = userService.updateAuthority(username, authorityName);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/{username}/authorities/{authority}")
    public ResponseEntity<Object> deleteUserAuthority(@PathVariable("username") String username, @PathVariable("authority") String authority) {
        userService.removeAuthority(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }
    @PutMapping(value = "/{username}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable("username") String username, @RequestBody UserInputDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        if (!userDetails.getUsername().equals(username) &&
                !userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You can only update your own profile.");
        }

        UserDto updatedUser = userService.updateUserProfile(username, dto);

        return ResponseEntity.ok(updatedUser);
    }
    @PostMapping("/{username}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable String username,
            @RequestPart("file") List<MultipartFile> files) {

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedInUsername.equals(username)) {
            throw new AccessDeniedException("You can only upload your own profile picture.");
        }

        if (files == null || files.size() != 1) {
            throw new BadRequestException("Exactly one file must be uploaded as a profile picture.");
        }

        MultipartFile file = files.get(0);
        userService.uploadProfilePicture(username, file);
        return ResponseEntity.ok("Profile picture uploaded successfully for user: " + username);
    }

    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) throws IOException {
        byte[] profilePicture = userService.getProfilePicture(username);

        if (profilePicture == null || profilePicture.length == 0) {
            throw new NotFoundException("No profile picture found for user: " + username);
        }

        String fileType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(profilePicture));
        MediaType mediaType = fileType != null ? MediaType.parseMediaType(fileType) : MediaType.APPLICATION_OCTET_STREAM;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDisposition(ContentDisposition.inline().filename("profile-picture.jpg").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(profilePicture);
    }
    @DeleteMapping("/{username}/profile-picture")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable String username) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedInUsername.equals(username)) {
            throw new AccessDeniedException("You can only delete your own profile picture.");
        }

        userService.deleteProfilePicture(username);
        return ResponseEntity.ok("Profile picture deleted successfully for user: " + username);
    }
}
