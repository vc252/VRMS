package fun.stockpiece.vehicle.rental.management.system.util;

import fun.stockpiece.vehicle.rental.management.system.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String secret = "vehicle-rental-management-system-secret-not-to-be-disclosed";
    private final long expiration = 60 * 60 * 1000;
    private SecretKey key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(key) // Use the SecretKey instead of SignatureAlgorithm and secret string
                .compact();
    }

    public String extractUsername(String token) {
        return null;
    }

    public boolean validateToken(String token, User userDetails) {
        return true;
    }
}
