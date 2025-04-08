package com.example.end.infrastructure.security.sec_dto;

import com.example.end.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class AuthInfo implements Authentication {

    private boolean authenticated;
    private final String username;
    private Set<User.Role> roles;


    public AuthInfo(String username, Set<User.Role> roles) {
        this.username = username;
        this.roles = roles;
    }

    public AuthInfo(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.roles = authorities.stream()
                .map(authority -> User.Role.valueOf(authority.getAuthority().replace("ROLE_", "")))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // Добавляем "ROLE_" для совместимости
                .collect(Collectors.toSet());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return username;
    }
}
