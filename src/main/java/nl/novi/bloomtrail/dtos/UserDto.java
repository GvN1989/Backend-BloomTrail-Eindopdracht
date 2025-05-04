package nl.novi.bloomtrail.dtos;

import nl.novi.bloomtrail.models.Authority;

import java.util.Set;

public class UserDto {
    private String username;
    private String email;
    private String fullName;
    private Boolean enabled;
    private String authority;
    public String profilePictureUrl;
    private String reportUrl;

    public UserDto(String username, String email, String fullName,Boolean enabled, String authority, String profilePictureUrl) {
        this.username = username;
        this.email = email;
        this.fullName= fullName;
        this.enabled = enabled;
        this.authority = authority;
        this.profilePictureUrl= profilePictureUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
