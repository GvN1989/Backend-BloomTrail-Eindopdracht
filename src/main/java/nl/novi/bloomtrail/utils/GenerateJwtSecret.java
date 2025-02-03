package nl.novi.bloomtrail.utils;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateJwtSecret {
        public static void main(String[] args) {
            SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
            String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
            System.out.println("Base64 Encoded Secret Key: " + base64Key);
        }
    }

