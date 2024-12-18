package do_an.backend_educheck.services.auth_service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${app.auth.tokenSecret}")
    public String tokenSecret;

    @Value("${app.auth.tokenExpirationMsec}")
    public Long tokenExpirationMsec;

    @Value("${app.auth.claimName}")
    public String roleCode;

    public boolean validateToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token);
            Date expirationDate = claimsJws.getBody().getExpiration();
            return !expirationDate.before(new Date());
        }catch (MalformedJwtException sie){
            System.out.println("MalformedJwtException: "+sie);
        }catch (ExpiredJwtException eie){
            System.out.println("ExpiredJwtException: "+eie);
        }catch (UnsupportedJwtException uie){
            System.out.println("UnsupportedJwtException: "+uie);
        }catch (IllegalArgumentException iie){
            System.out.println("IllegalArgumentException: "+iie);
        }catch (Exception ex){
            System.out.println("Exception: "+ex);
        }
        return false;
    }

    public String createJwtToken(String email, String roles) {
        return buildJwtToken(email, roles);
    }

    private String buildJwtToken(String email, String roles) {
        final var now = new Date();
        final var expireDate = new Date(now.getTime() + tokenExpirationMsec);
        Claims claims = Jwts.claims()
                .setSubject(email);
        claims.put(roleCode, roles);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return (Claims) Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8))).parse(token).getBody();
    }

    public Claims decodeJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public void removeTokenOnCookie (String tokenCookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(tokenCookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}
