package org.burza.soap_api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Auth {
    private static String secret = "your_jwt_secret_here";

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
