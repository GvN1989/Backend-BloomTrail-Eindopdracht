package nl.novi.bloomtrail.mappers;

import nl.novi.bloomtrail.dtos.UserDto;
import nl.novi.bloomtrail.dtos.UserInputDto;
import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.models.Authority;
import nl.novi.bloomtrail.models.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.isEnabled(),
                user.getAuthority() != null ? user.getAuthority().getAuthority() : null,
                user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null

        );
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public static User toUserEntity(UserInputDto userInputDto) {
        User user = new User();
        user.setUsername(userInputDto.getUsername());
        user.setPassword(userInputDto.getPassword());
        user.setEnabled(userInputDto.getEnabled());
        user.setApikey(userInputDto.getApikey());
        user.setEmail(userInputDto.getEmail());
        user.setFullName(userInputDto.getFullName());

        if (userInputDto.getAuthority() == null || userInputDto.getAuthority().isBlank()) {
            throw new BadRequestException("User must be assigned a valid role.");}

            Authority authority = new Authority();
            authority.setAuthority(userInputDto.getAuthority());
            authority.setUser(user);
            authority.setUsername(user.getUsername());

            user.setAuthority(authority);

        return user;
    }

}
