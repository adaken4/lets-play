package com.github.adaken4.lets_play.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * Loads user details from the database by email address during login attempts.
 * Maps User entity to Spring Security's UserDetails for authorization.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details by email address for Spring Security authentication.
     * 
     * @param email the user's email address (used as username)
     * @return UserDetails containing email, password, and roles
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user by email from database, throw exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Return UserDetailsImpl instead of the default builder
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
        return new UserDetailsImpl(user, authorities);
        
    }

    public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {
        private final String id;

        public UserDetailsImpl(User user, Collection<? extends GrantedAuthority> authorities) {
            super(user.getEmail(), user.getPassword(), authorities);
            this.id = user.getId();
        }

        public String getId() {
            return id;
        }
    }
}
