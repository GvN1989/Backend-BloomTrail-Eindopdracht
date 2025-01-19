package nl.novi.bloomtrail.exceptions;

public class UsernameNotFoundException extends RuntimeException{

    public UsernameNotFoundException(String username) {
        super("Cannot find user " + username);
    }
}
