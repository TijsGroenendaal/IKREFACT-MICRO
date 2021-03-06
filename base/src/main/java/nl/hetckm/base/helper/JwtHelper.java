package nl.hetckm.base.helper;

import io.jsonwebtoken.*;
import nl.hetckm.base.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtHelper {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Long lifetime;

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    public String createJwtForClaims(String subject, Map<String, Object> claims) {
        Calendar calendar = Calendar.getInstance();
        long millis = Instant.now().toEpochMilli() + lifetime;
        calendar.setTimeInMillis(millis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setExpiration(calendar.getTime())
                .setIssuedAt(new Date())
                .setIssuer("bouncer-api")
                .setAudience("bouncer-client")
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Jwt decode(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            Date issuedAt = jwt.getBody().getIssuedAt();
            Date expiration = jwt.getBody().getExpiration();
            JwsHeader<?> headers = jwt.getHeader();
            Map<String, Object> claimsMap = new HashMap<>();
            claimsMap.put(AUTHORITIES_CLAIM_NAME, jwt.getBody().get(AUTHORITIES_CLAIM_NAME));
            claimsMap.put("sub", jwt.getBody().getSubject());
            return new Jwt("bouncer-api", issuedAt.toInstant(), expiration.toInstant(), headers, claimsMap);
        } catch (JwtException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

}
