package pl.pb.finansista.frontend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JwtCookieFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            String token = cookie.getValue();
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                    JsonNode payload = objectMapper.readTree(payloadJson);
                    
                    String principalName = payload.has("name") ? payload.get("name").asText() : 
                                          (payload.has("sub") ? payload.get("sub").asText() : null);
                    
                    String role = payload.has("role") ? payload.get("role").asText() : null;
                    
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }

                    if (principalName != null) {
                        UsernamePasswordAuthenticationToken auth = 
                                new UsernamePasswordAuthenticationToken(principalName, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
