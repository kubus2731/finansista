package pl.pb.finansista.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import pl.pb.finansista.user.User;

@Service
public class JwtService {

  @Value("${app.security.jwt.secret-key}")
  private String secretKey;

  @Value("${app.security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${app.security.jwt.cookie-name}")
  private String jwtCookieName;

  @Value("${app.security.jwt.cookie-secure}")
  private boolean jwtCookieSecure;

  public String generateToken(User user) {
    return Jwts.builder()
        .subject(user.getExternalId().toString())
        .claim("role", user.getRole().getName())
        .claim("name", user.getName() + " " + user.getSurname())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey())
        .compact();
  }

  public ResponseCookie generateJwtCookie(String token) {
    return ResponseCookie.from(jwtCookieName, token)
        .path("/")
        .maxAge(jwtExpiration / 1000)
        .httpOnly(true)
        .secure(jwtCookieSecure)
        .sameSite("Strict")
        .build();
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(jwtCookieName, "").path("/").httpOnly(true).maxAge(0).build();
  }

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
    return cookie != null ? cookie.getValue() : null;
  }

  public String extractSubject(String token) {
    return Jwts.parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public Boolean isTokenValid(String token) {
    try {
      Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
