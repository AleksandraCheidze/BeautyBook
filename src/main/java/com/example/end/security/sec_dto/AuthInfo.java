package com.example.end.security.sec_dto;

import com.example.end.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AuthInfo implements Authentication {


    private boolean authenticated;
    private final String username;
    private Set<User.Role> roles;

    public AuthInfo(String username, Set<String> roles) {
        this.username = username;
        this.roles = convertStringRolesToEnum(roles);
    }

    private Set<User.Role> convertStringRolesToEnum(Set<String> stringRoles) {
        Set<User.Role> enumRoles = new HashSet<>();
        for (String stringRole : stringRoles) {
            enumRoles.add(User.Role.valueOf(stringRole));
        }
        return enumRoles;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (User.Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }
        return authorities;
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


