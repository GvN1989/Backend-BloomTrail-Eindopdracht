package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.mappers.UserMapper;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.AuthorityRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import nl.novi.bloomtrail.utils.RandomStringGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final ValidationHelper validationHelper;
    private final AuthorityRepository authorityRepository;
    private final FileService fileService;
    private final DownloadService downloadService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ValidationHelper validationHelper, AuthorityRepository authorityRepository, FileService fileService, DownloadService downloadService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.authorityRepository = authorityRepository;
        this.fileService = fileService;
        this.downloadService = downloadService;
        this.passwordEncoder = passwordEncoder;
    }


    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    public UserDto getUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (user.getAuthority() == null) {
            throw new BadRequestException("User has no assigned role.");
        }

        return UserMapper.toUserDto(user);
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    public String createUser(UserInputDto userInputDto) {
        String randomString = RandomStringGenerator.generateAlphaNumeric(20);
        userInputDto.setApikey(randomString);
        User newUser = userRepository.save(UserMapper.toUserEntity(userInputDto));
        return newUser.getUsername();
    }

    public void deleteUser(String username) {
        validationHelper.validateUser(username);
        userRepository.deleteById(username);
    }
    @Transactional
    public UserDto updateUserProfile(String username, UserInputDto dto) {
        User user = validationHelper.validateUser(username);

        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            throw new BadRequestException("Username cannot be changed.");
        }

        userRepository.save(user);

        return UserMapper.toUserDto(user);

    }

    public Authority getAuthority(String username) {
        User user = validationHelper.validateUser(username);
        return user.getAuthority();
    }

    public UserDto updateAuthority(String username, String newAuthority) {
        User user = validationHelper.validateUser(username);
        validationHelper.validateAuthority(newAuthority);

        if (user.getAuthority() != null) {
            authorityRepository.delete(user.getAuthority());
        }

        Authority newRole = new Authority();
        newRole.setAuthority(newAuthority);
        newRole.setUser(user);
        newRole.setUsername(user.getUsername());

        user.setAuthority(newRole);

        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    public void removeAuthority(String username) {
        User user = validationHelper.validateUser(username);

        if (user.getAuthority() == null) {
            throw new NotFoundException("User " + username + " does not have an assigned role.");
        }

        authorityRepository.delete(user.getAuthority());

        user.setAuthority(null);
        userRepository.save(user);
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

        if (user.getProfilePicture() == null) {
            throw new NotFoundException("User '" + username + "' does not have a profile picture to delete.");
        }

        try {
            File profilePicture = user.getProfilePicture();

            Path filePath = Paths.get(profilePicture.getUrl());
            Files.deleteIfExists(filePath);

            user.setProfilePicture(null);
            userRepository.save(user);

            fileService.deleteFile(profilePicture);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete profile picture file for user '" + username + "'", e);
        }
    }

}
