package com.example.end.infrastructure.security.sec_filter;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.infrastructure.security.sec_dto.AuthInfo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@Slf4j
public class TokenFilter extends GenericFilterBean {

    private final TokenService service;

    public TokenFilter(TokenService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        
        if (token != null) {
            log.debug("Found token in request");
            if (service.validateAccessToken(token)) {
                log.debug("Token is valid");
                Claims claims = service.getAccessClaims(token);
                AuthInfo authInfo = service.generateAuthInfo(claims);
                authInfo.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authInfo);
                log.debug("Authentication set in SecurityContext");
            } else {
                log.debug("Token validation failed");
            }
        } else {
            log.debug("No token found in request");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Access-Token".equals(cookie.getName())) {
                    log.debug("Found token in cookies");
                    return cookie.getValue();
                }
            }
        }

        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            log.debug("Found token in Authorization header");
            return bearer.substring(7);
        }
        return null;
    }
}