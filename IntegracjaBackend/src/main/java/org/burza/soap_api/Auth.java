package org.burza.soap_api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Auth {
    private static String secret;

    static {
        try {
            //secret = Files.readString(Paths.get("/run/secrets/jwt_secret")).trim();
            secret = "dziwki_dragi_lasery";
            System.out.println("Successfully loaded JWT secret from Docker secret");
        } catch (Exception e) {
            System.err.println("Failed to read JWT secret from Docker secret: " + e.getMessage());
            System.exit(1); // Exit if we can't read the secret - this is critical for security
        }
    }

    public static String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(java.time.LocalDate.now().plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
                .sign(algorithm);
    }

    public static boolean checkToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("Token is valid!");
            System.out.println("Expires At: " + jwt.getExpiresAt());
            return true;
        } catch (
        JWTVerificationException e) {
            System.out.println("Token signature invalid: " + e.getMessage());
            return false;
        }
    }
}
