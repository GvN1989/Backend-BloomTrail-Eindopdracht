package nl.novi.bloomtrail.dtos;

public class AuthenticatedUserDto {

    private String username;
    private String authority;
    private boolean authenticated;

    public AuthenticatedUserDto(String username, String authority, boolean authenticated) {
        this.username = username;
        this.authority = authority;
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthority() {
        return authority;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
