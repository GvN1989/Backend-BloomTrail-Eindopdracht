package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.services.UserService;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping(value="/users")
public class UserController {

    private final UserService userService;

    private final ValidationHelper validationHelper;

    public UserController(UserService userService, ValidationHelper validationHelper) {
        this.userService = userService;
        this.validationHelper = validationHelper;
    }

    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userProfiles = userService.getUsers();
        return ResponseEntity.ok().body(userProfiles);
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        UserDto userDto = userService.getUser(username);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping(value = "/")
    public ResponseEntity<UserDto> createUser(@RequestBody UserInputDto dto) throws BadRequestException {

        validationHelper.validateUserInput(dto);

        String newUsername = userService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUsername)
                .toUri();

        UserDto createdUser = userService.getUser(newUsername);

        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping(value = "/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username, @RequestBody UserInputDto dto) {
        userService.updateUser(username, dto);

        UserDto updatedUser = userService.getUser(username);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> getUserAuthorities(@PathVariable("username") String username) {
        return ResponseEntity.ok().body(userService.getAuthorities(username));
    }


    @PutMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> updateUserAuthority(@PathVariable("username") String username, @RequestBody Map<String, Object> fields) {
        try {
            String authorityName = (String) fields.get("authority");
            validationHelper.validateAuthority(authorityName);
            if (!userService.userExists(username)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found: " + username);
            }

            UserDto updatedUser = userService.updateAuthority(username, authorityName);

            return ResponseEntity.ok("User " + username + " updated to role " + authorityName);
        }
        catch (Exception ex) {
            throw new BadRequestException();
        }
    }

    @DeleteMapping(value = "/{username}/authorities/{authority}")
    public ResponseEntity<Object> deleteUserAuthority(@PathVariable("username") String username, @PathVariable("authority") String authority) {
        userService.removeAuthority(username, authority);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{username}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable String username,
            @RequestPart("file") MultipartFile file) {
        userService.uploadProfilePicture(username, file);
        return ResponseEntity.ok("Profile picture uploaded successfully for user: " + username);
    }

    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) throws IOException {
        byte[] profilePicture = userService.getProfilePicture(username);

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
        userService.deleteProfilePicture(username);
        return ResponseEntity.ok("Profile picture deleted successfully for user: " + username);
    }


}
