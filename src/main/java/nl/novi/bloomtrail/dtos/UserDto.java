package nl.novi.bloomtrail.dtos;

import nl.novi.bloomtrail.models.Authority;

import java.util.Set;

public class UserDto {
    private String username;
    private String email;
    private Boolean enabled;
    private Set<Authority> authorities;
    public String profilePictureUrl;

    public UserDto(String username, String email, Boolean enabled, Set<Authority> authorities, String profilePictureUrl) {
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.authorities = authorities;
        this.profilePictureUrl= profilePictureUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }
}
