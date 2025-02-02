package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.exceptions.UsernameNotFoundException;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityValidationHelper validationHelper;
    private final FileService fileService;
    private final DownloadService downloadService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityValidationHelper validationHelper, FileService fileService, DownloadService downloadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validationHelper = validationHelper;
        this.fileService = fileService;
        this.downloadService = downloadService;
    }


    public List<UserDto> getUsers() {
        List<UserDto> collection = new ArrayList<>();
        List<User> list = userRepository.findAll();
        for (User user : list) {
            collection.add(fromUser(user));
        }
        return collection;
    }

    public UserDto getUser(String username) {
        UserDto dto = new UserDto();
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent()) {
            dto = fromUser(user.get());
        } else {
            throw new UsernameNotFoundException(username);
        }
        return dto;
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    public String createUser(UserDto userDto) {

        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(hashedPassword);

        User newUser = toUser(userDto);

        if (newUser.getAuthorities() == null || newUser.getAuthorities().isEmpty()) {
            newUser.setAuthorities(Set.of(new Authority(newUser.getUsername(), "ROLE_USER")));
        }


        newUser = userRepository.save(newUser);
        return newUser.getUsername();
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public void updateUser(String username, UserDto newUser) {
        if (!userRepository.existsById(username)) throw new RecordNotFoundException();
        User user = userRepository.findById(username).get();
        user.setPassword(newUser.getPassword());
        userRepository.save(user);
    }

    public Set<Authority> getAuthorities(String username) {
        if (!userRepository.existsById(username)) throw new UsernameNotFoundException(username);
        User user = userRepository.findById(username).get();
        UserDto userDto = fromUser(user);
        return userDto.getAuthorities();
    }

    public void addAuthority(String username, String authority) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<Authority> authorities = user.getAuthorities();

        if (authorities.stream().noneMatch(a -> a.getAuthority().equalsIgnoreCase(authority))) {
            authorities.add(new Authority(username, authority));
        }

        user.setAuthorities(authorities);
        userRepository.save(user);
    }

    public void removeAuthority(String username, String authority) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<Authority> authorities = user.getAuthorities();

        Authority authorityToRemove = authorities.stream()
                .filter(a -> a.getAuthority().equalsIgnoreCase(authority))
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authority not found: " + authority));

        authorities.remove(authorityToRemove);

        if (authorities.isEmpty()) {
            authorities.add(new Authority(username, "ROLE_USER"));
        }

        user.setAuthorities(authorities);
        userRepository.save(user);
    }

    public static UserDto fromUser(User user) {

        var dto = new UserDto();

        dto.username = user.getUsername();
        dto.password = user.getPassword();
        dto.enabled = user.isEnabled();
        dto.apikey = user.getApikey();
        dto.email = user.getEmail();
        dto.authorities = user.getAuthorities();

        return dto;
    }

    public User toUser(UserDto userDto) {

        var user = new User();

        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEnabled(userDto.getEnabled());
        user.setApikey(userDto.getApikey());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public void uploadProfilePicture(String username, MultipartFile file) {
        User user = validationHelper.validateUser(username);

        File profilePicture = fileService.saveFile(file, FileContext.PROFILE_PICTURE, user);
        user.setProfilePicture(profilePicture);
        userRepository.save(user);
    }

    public byte[] getProfilePicture(String username) {
        User user = validationHelper.validateUser(username);

        if (user.getProfilePicture() == null) {
            throw new IllegalArgumentException("Profile picture not set");
        }

        return downloadService.downloadFile(user.getProfilePicture().getUrl());
    }

    public void deleteProfilePicture(String username) {
        User user = validationHelper.validateUser(username);

        if (user.getProfilePicture() != null) {
            try {
                fileService.deleteFilesForParentEntity(user.getProfilePicture().getFileId());
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete the profile picture for user '" + username + "'", e);
            }
            user.setProfilePicture(null);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("User '" + username + "' does not have a profile picture to delete");
        }
    }

}
