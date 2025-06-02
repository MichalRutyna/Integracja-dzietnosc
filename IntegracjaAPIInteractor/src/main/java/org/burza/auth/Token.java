package org.burza.auth;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.auth0.jwt.algorithms.Algorithm;

@Component
public class Token {
    private static String secret;

    static {
        try {
            secret = Files.readString(Paths.get("/run/secrets/jwt_secret")).trim();
            System.out.println("Successfully loaded JWT secret from Docker secret");
        } catch (Exception e) {
            System.err.println("Failed to read JWT secret from Docker secret: " + e.getMessage());
            try {
                secret = Files.readString(Paths.get("../secrets/jwt_secret.txt")).trim();
                System.out.println("Successfully loaded JWT secret from file");
            } catch (IOException ex) {
                System.err.println("Failed to read JWT secret from file: " + ex.getMessage());
                secret = "your_jwt_secret_here";
                System.err.println("Using default JWT secret");
            }
        }
    }

    public static String getToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(java.time.LocalDate.now().plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
                .sign(algorithm);
    }
}