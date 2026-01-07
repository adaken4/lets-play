package com.github.adaken4.lets_play.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component // Runs automatically on application startup
public class DatabaseLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.setup.email}")
    private String adminEmail;
    @Value("${admin.setup.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Only create the system admin user if it does not already exist
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    UUID.randomUUID().toString(),
                    "System Admin",
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    "ADMIN");
            // Persist the initial system admin user into the database
            userRepository.save(admin);
        } else {
            System.out.println("System admin user already exists.");
        }
    }
}
