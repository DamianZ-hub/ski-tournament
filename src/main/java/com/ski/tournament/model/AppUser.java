package com.ski.tournament.model;

import com.ski.tournament.config.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppUser implements UserDetails {

    private Person person;

    public AppUser(final Person person) {
        this.person = person;
    }

    public AppUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final UserRole userRole : person.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(userRole.name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }
    @Override
    public String getUsername() {
        if (this.person == null) {
            return null;
        }
        return this.person.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return person.isEnabled();
    }

    public Person getPerson() {
        return person;
    }


    @Override
    public String toString() {
        return "CustomUserDetails [user=" + person + "]";
    }
}
