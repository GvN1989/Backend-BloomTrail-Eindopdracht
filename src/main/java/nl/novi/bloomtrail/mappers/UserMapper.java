package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getAuthorities(),
                user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null
        );
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public static UserInputDto toUserInputDto(User user) {
        return new UserInputDto(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.getApikey(),
                user.getEmail(),
                user.getAuthorities()
        );
    }

    public static User toUserEntity(UserInputDto userInputDto) {
        User user = new User();
        user.setUsername(userInputDto.getUsername());
        user.setPassword(userInputDto.getPassword());
        user.setEnabled(userInputDto.getEnabled());
        user.setApikey(userInputDto.getApikey());
        user.setEmail(userInputDto.getEmail());

        return user;
    }


}
