package com.unigo.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Autowired
    private JwtConfig config;

    public String generateAccessToken(UserDetails userDetails, int userId) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("userId", userId)
                .withClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withIssuer(config.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + config.getAccessTokenExpires()))
                .sign(Algorithm.HMAC256(config.getSecret()));
    }

    public String generateRefreshToken(UserDetails userDetails, int userId) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("userId", userId)
                .withClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withIssuer(config.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + config.getRefreshTokenExpires()))
                .sign(Algorithm.HMAC256(config.getSecret()));
    }

    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(config.getSecret()))
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    // Verifica firma + issuer + expiracion. Lanza JWTVerificationException si
    // alguno falla. Devuelve el DecodedJWT para que el caller pueda leer claims.
    private DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(config.getSecret()))
                .withIssuer(config.getIssuer())
                .build()
                .verify(token);
    }

    // A partir de un refresh token VALIDO (firma + tipo "refresh"), genera un
    // nuevo access token con los mismos claims de identidad. Anteriormente se
    // usaba JWT.decode (sin verificacion), lo que permitia falsificaciones.
    public String generateAccessToken(String refreshToken) {
        DecodedJWT jwt = verifyToken(refreshToken);
        if (!"refresh".equals(jwt.getClaim("type").asString())) {
            throw new JWTVerificationException("El token proporcionado no es un refresh token");
        }
        return JWT.create()
                .withSubject(jwt.getSubject())
                .withClaim("userId", jwt.getClaim("userId").asInt())
                .withClaim("roles", jwt.getClaim("roles").asList(String.class))
                .withIssuedAt(new Date())
                .withIssuer(config.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + config.getAccessTokenExpires()))
                .sign(Algorithm.HMAC256(config.getSecret()));
    }

    // Rotacion del refresh token: valida el actual y emite uno nuevo. La duracion
    // ahora si es la del refresh (antes usaba la del access por bug).
    public String generateRefreshToken(String refreshToken) {
        DecodedJWT jwt = verifyToken(refreshToken);
        if (!"refresh".equals(jwt.getClaim("type").asString())) {
            throw new JWTVerificationException("El token proporcionado no es un refresh token");
        }
        return JWT.create()
                .withSubject(jwt.getSubject())
                .withClaim("userId", jwt.getClaim("userId").asInt())
                .withClaim("roles", jwt.getClaim("roles").asList(String.class))
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withIssuer(config.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + config.getRefreshTokenExpires()))
                .sign(Algorithm.HMAC256(config.getSecret()));
    }
}
