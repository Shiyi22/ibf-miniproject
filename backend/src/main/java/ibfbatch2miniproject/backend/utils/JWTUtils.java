package ibfbatch2miniproject.backend.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ibfbatch2miniproject.backend.model.Login;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtils {

    // @Value("${jwt.secret}")
    // private String secret;

    private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256); 

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Login login) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, login.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secret)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractExpirationDate(token);
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, Login login) {
        String username = extractUsername(token);
        return (username.equals(login.getUsername()) && !isTokenExpired(token));
    }
    
}
