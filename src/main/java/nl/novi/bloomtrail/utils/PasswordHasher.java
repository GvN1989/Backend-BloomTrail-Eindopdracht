package nl.novi.bloomtrail.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "bananenneus";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("BCrypt hash: " + encodedPassword);
    }
}

