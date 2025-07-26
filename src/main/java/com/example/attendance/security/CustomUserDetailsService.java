package com.example.attendance.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{noop}password123")  // {noop} means no password encoding
                    .authorities(List.of(() -> "ROLE_ADMIN")) // admin role
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
