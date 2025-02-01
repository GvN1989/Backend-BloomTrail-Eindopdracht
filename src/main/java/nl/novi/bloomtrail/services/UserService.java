package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.dtos.CoachingProgramRoleDto;
import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.enums.FileContext;
import nl.novi.bloomtrail.exceptions.UsernameNotFoundException;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.File;
import nl.novi.bloomtrail.models.User;
import nl.novi.bloomtrail.repositories.UserRepository;
import nl.novi.bloomtrail.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import nl.novi.bloomtrail.helper.EntityValidationHelper;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final DownloadService downloadService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final EntityValidationHelper validationHelper;

    public UserService(UserRepository userRepository, FileService fileService, DownloadService downloadService, EntityValidationHelper validationHelper) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.downloadService = downloadService;
        this.validationHelper = validationHelper;
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
        if (user.isPresent()){
            dto = fromUser(user.get());
        }else {
            throw new UsernameNotFoundException(username);
        }
        return dto;
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    public String createUser(UserDto userDto) {

        String randomString = RandomStringGenerator.generateAlphaNumeric(20);
        userDto.setApikey(randomString);

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User newUser = userRepository.save(toUser(userDto));
        return newUser.getUsername();
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public void updateUser(String username, UserDto newUser) {
        if (!userRepository.existsById(username)) throw new UsernameNotFoundException(username);
        User user = userRepository.findById(username).get();

        if(newUser.getPassword()!=null && !newUser.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(newUser.getPassword());

            user.setPassword(newUser.getPassword());
        }
        userRepository.save(user);
    }

    public Set<Authority> getAuthorities(String username) {
        if (!userRepository.existsById(username)) throw new UsernameNotFoundException(username);
        User user = userRepository.findById(username).get();
        UserDto userDto = fromUser(user);
        return userDto.getAuthorities();
    }

    public void addAuthority(String username, String authority) {

        if (!userRepository.existsById(username)) throw new UsernameNotFoundException(username);
        User user = userRepository.findById(username).get();
        user.addAuthority(new Authority(username, authority));
        userRepository.save(user);
    }

    public void removeAuthority(String username, String authority) {
        if (!userRepository.existsById(username)) throw new UsernameNotFoundException(username);
        User user = userRepository.findById(username).get();
        Authority authorityToRemove = user.getAuthorities().stream().filter((a) -> a.getAuthority().equalsIgnoreCase(authority)).findAny().get();
        user.removeAuthority(authorityToRemove);
        userRepository.save(user);
    }

    public static UserDto fromUser(User user){

        var dto = new UserDto();

        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEnabled(user.isEnabled());
        dto.setApikey(user.getApikey());
        dto.setEmail(user.getEmail());

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
