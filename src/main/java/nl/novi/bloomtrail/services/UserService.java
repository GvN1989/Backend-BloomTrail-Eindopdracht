package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.exceptions.ConflictException;
import nl.novi.bloomtrail.exceptions.NotFoundException;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.helper.AccessValidator;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.mappers.UserMapper;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.AuthorityRepository;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import nl.novi.bloomtrail.repositories.FileRepository;
import nl.novi.bloomtrail.repositories.UserRepository;
import nl.novi.bloomtrail.utils.RandomStringGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FileRepository fileRepository;
    private final ValidationHelper validationHelper;
    private final AccessValidator accessValidator;
    private final AuthorityRepository authorityRepository;
    private final FileService fileService;
    private final DownloadService downloadService;
    private final PasswordEncoder passwordEncoder;
    private final CoachingProgramRepository coachingProgramRepository;

    public UserService(UserRepository userRepository, FileRepository fileRepository, ValidationHelper validationHelper, AccessValidator accessValidator, AuthorityRepository authorityRepository, FileService fileService, DownloadService downloadService, PasswordEncoder passwordEncoder, CoachingProgramRepository coachingProgramRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.validationHelper = validationHelper;
        this.accessValidator = accessValidator;
        this.authorityRepository = authorityRepository;
        this.fileService = fileService;
        this.downloadService = downloadService;
        this.passwordEncoder = passwordEncoder;
        this.coachingProgramRepository = coachingProgramRepository;
    }


    public List<UserDto> getUsers() {

        String requester = SecurityContextHolder.getContext().getAuthentication().getName();

        if (accessValidator.isAdmin()) {
            List<User> users = userRepository.findAll();
            return UserMapper.toUserDtoList(users);
        }

        if (accessValidator.isCoach()) {
            List<CoachingProgram> programs = coachingProgramRepository.findAllByCoach_Username(requester);

            List<User> clients = programs.stream()
                    .map(CoachingProgram::getClient)
                    .distinct()
                    .toList();

            return UserMapper.toUserDtoList(clients);
        }

        throw new AccessDeniedException("Only coaches and admins can access user lists.");
    }

    public UserDto getUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (user.getAuthority() == null) {
            throw new BadRequestException("User has no assigned role.");
        }

        accessValidator.validateSelfOrAdminAccess(username);

        return UserMapper.toUserDto(user);
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    public String createUser(UserInputDto userInputDto) {
        if (userExists(userInputDto.getUsername())) {
            throw new ConflictException("Username already exists.");
        }

        String randomString = RandomStringGenerator.generateAlphaNumeric(20);
        userInputDto.setApikey(randomString);

        String encodedPassword = passwordEncoder.encode(userInputDto.getPassword());
        userInputDto.setPassword(encodedPassword);

        User newUser = userRepository.save(UserMapper.toUserEntity(userInputDto));
        return newUser.getUsername();
    }
    public void deleteUser(String username) {
        User user = validationHelper.validateUser(username);
        userRepository.delete(user);
    }
    @Transactional
    public UserDto updateUserProfile(String username, UserInputDto dto) {
        accessValidator.validateSelfOrAdminAccess(username);

        User user = validationHelper.validateUser(username);

        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
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
        accessValidator.validateAuthority(newAuthority);

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
        accessValidator.validateSelfOrAdminAccess(username);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file uploaded.");
        }

        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new BadRequestException("Only JPEG and PNG image files are allowed.");
        }

        User user = validationHelper.validateUser(username);
        if (user.getProfilePicture() != null) {

            File oldFile = user.getProfilePicture();

            user.setProfilePicture(null);
            userRepository.save(user);

            fileService.deleteFile(oldFile);
            fileRepository.delete(oldFile);
        }

        File savedFile = fileService.saveFile(file, FileContext.PROFILE_PICTURE, user);
        fileRepository.flush();

        user.setProfilePicture(savedFile);
        userRepository.save(user);
    }

    public byte[] getProfilePicture(String username) {
        User user = validationHelper.validateUser(username);

        if (user.getProfilePicture() == null) {
            throw new NotFoundException("The requested profile picture is not set");
        }

        return downloadService.downloadFile(user.getProfilePicture().getUrl());
    }

    public void deleteProfilePicture(String username) {
        accessValidator.validateSelfOrAdminAccess(username);

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
