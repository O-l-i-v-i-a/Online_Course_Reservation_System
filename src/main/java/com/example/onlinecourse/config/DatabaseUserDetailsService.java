package com.example.onlinecourse.config;

import com.example.onlinecourse.domain.AppUser;
import com.example.onlinecourse.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Extract role and convert to ROLE_* format
        String roleWithPrefix = "ROLE_" + appUser.getRole().toString().toUpperCase();

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(new SimpleGrantedAuthority(roleWithPrefix))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
