package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.exceptions.RecordNotFoundException;
import nl.novi.bloomtrail.exceptions.UsernameNotFoundException;
import nl.novi.bloomtrail.helper.ValidationHelper;
import nl.novi.bloomtrail.mappers.UserMapper;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.*;
import nl.novi.bloomtrail.repositories.UserRepository;
import nl.novi.bloomtrail.utils.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final ValidationHelper validationHelper;
    private final FileService fileService;
    private final DownloadService downloadService;

    public UserService(UserRepository userRepository, ValidationHelper validationHelper, FileService fileService, DownloadService downloadService) {
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.fileService = fileService;
        this.downloadService = downloadService;
    }


    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    public UserDto getUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
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
        userRepository.deleteById(username);
    }

    public void updateUser(String username, UserInputDto newUser) {
        if (!userRepository.existsById(username)) throw new RecordNotFoundException();
        User user = userRepository.findById(username).get();
        user.setPassword(newUser.getPassword());
        userRepository.save(user);
    }

    public Set<Authority> getAuthorities(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return user.getAuthorities();
    }

    public UserDto updateAuthority(String username, String newAuthority) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RecordNotFoundException("User not found: " + username));

        List<String> validRoles = Arrays.asList("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN");
        if (!validRoles.contains(newAuthority)) {
            throw new IllegalArgumentException("Invalid role: " + newAuthority);
        }

        user.getAuthorities().clear();
        user.addAuthority(new Authority(username, newAuthority));

        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    public void removeAuthority(String username, String authority) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Authority authorityToRemove = user.getAuthorities().stream()
                .filter(a -> a.getAuthority().equalsIgnoreCase(authority))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Authority not found: " + authority));

        user.removeAuthority(authorityToRemove);
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
