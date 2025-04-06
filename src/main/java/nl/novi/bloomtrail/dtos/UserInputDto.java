package nl.novi.bloomtrail.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import nl.novi.bloomtrail.models.Authority;

import java.util.Set;

public class UserInputDto {

    @NotEmpty(message = "Username cannot be empty")
    public String username;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    public String password;
    public Boolean enabled;
    public String apikey;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    public String email;
    public String fullName;

    @NotEmpty(message = "At least one authority must be assigned")
    public String authority;

    public UserInputDto(String username, String password, Boolean enabled, String apikey, String email, String authority, String fullName) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.apikey = apikey;
        this.email = email;
        this.fullName = fullName;
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getApikey() {
        return apikey;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
