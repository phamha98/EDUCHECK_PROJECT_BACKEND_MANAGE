package do_an.backend_educheck.configs.jwt;

import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.services.auth_service.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Value("${app.auth.tokenCookieName}")
    public String tokenCookieName;

    @Value("${app.auth.claimName}")
    public String roleCode;

    private String[] publicEndpoints = {"login", "signup", "home", "forgot-password", "reset-password", "access-reset", "sync-user-system",};

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("student", null, new ArrayList<>()));
        filterChain.doFilter(httpRequest, response);

//        Cookie[] cookies = httpRequest.getCookies();
//        String pathName = httpRequest.getParameter("pathname");
//        String headerToken = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
//
//        //        System.out.println("pathName==>" + pathName);
//
//        String[] paths = pathName.substring(1).split("/");
//
//        if (Arrays.asList(publicEndpoints).contains(paths[0]) || headerToken == null || !headerToken.startsWith(
//                "Bearer")) {
//            SecurityContextHolder.getContext().setAuthentication(
//                    new UsernamePasswordAuthenticationToken("student", null, new ArrayList<>()));
//            filterChain.doFilter(httpRequest, response);
//            return;
//        }
//
//
//        if (cookies != null) {
//            String token = getJwtTokenFromCookie(cookies);
//
//            if (token == null) {
//                token = headerToken.replace("Bearer ", "");
//            }
//
//            if (StringUtils.isBlank(token)) {
//                throw new ArgumentException("Bạn không có quyền truy cập.");
//            }
//
//            //            boolean isValidToken = tokenProvider.validateToken(token);
//            //
//            //            if (!isValidToken) {
//            //                throw new ArgumentException("Bạn không có quyền truy cập.");
//            //            }
//
//            try {
//                tokenProvider.decodeJwt(token);
//                UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } catch (Exception e) {
//                throw new ArgumentException("Bạn không có quyền truy cập.");
//            }
//        }
//
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken("student", null, new ArrayList<>()));
//        filterChain.doFilter(httpRequest, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = tokenProvider.parseToken(token);

        if (claims != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (claims.get(roleCode) != null) {
                authorities.add(new SimpleGrantedAuthority((String) claims.get(roleCode)));
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), true, authorities);
            authentication.setDetails(claims);
            return authentication;
        }

        return null;
    }

    public String getJwtTokenFromCookie(Cookie[] cookies) {

        for (Cookie cookie : cookies) {
            if (tokenCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}