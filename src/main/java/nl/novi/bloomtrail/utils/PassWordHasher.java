package nl.novi.bloomtrail.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassWordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("bananenneus");
        System.out.println("New Hashed Password: " + hashedPassword);
    }

}
