package nl.novi.bloomtrail.controllers;

import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.services.UserService;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getUsers() {

        List<UserDto> userDtos = userService.getUsers();

        return ResponseEntity.ok().body(userDtos);
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {

        UserDto optionalUser = userService.getUser(username);


        return ResponseEntity.ok().body(optionalUser);

    }

    @PostMapping(value = "/")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) throws BadRequestException {

        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new BadRequestException( "Username cannot be null or empty");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BadRequestException( "Password cannot be null or empty");
        }

        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new BadRequestException("Email cannot be null or empty");
        }

        String newUsername = userService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUsername)
                .toUri();

        UserDto createdUser = userService.getUser(newUsername);

        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping(value = "/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username, @RequestBody UserDto dto) {

        userService.updateUser(username, dto);

        return ResponseEntity.noContent().build();
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


    @PostMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> addUserAuthority(@PathVariable("username") String username, @RequestBody Map<String, Object> fields) {
        try {
            String authorityName = (String) fields.get("authority");
            userService.addAuthority(username, authorityName);
            return ResponseEntity.noContent().build();
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
