package com.publish.service;

import com.publish.model.User;
import com.publish.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Initialize default users on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Chỉ tạo user nếu database trống
        if (userRepository.count() > 0) {
            return;
        }

        // Tạo ADMIN user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(bcrypt.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);
        System.out.println("✅ Created ADMIN user: admin / admin123");

        // Tạo EDITOR user
        User editor = new User();
        editor.setUsername("editor");
        editor.setEmail("editor@example.com");
        editor.setPasswordHash(bcrypt.encode("editor123"));
        editor.setRole(User.Role.EDITOR);
        userRepository.save(editor);
        System.out.println("✅ Created EDITOR user: editor / editor123");

        // Tạo WRITER user
        User writer = new User();
        writer.setUsername("writer");
        writer.setEmail("writer@example.com");
        writer.setPasswordHash(bcrypt.encode("writer123"));
        writer.setRole(User.Role.WRITER);
        userRepository.save(writer);
        System.out.println("✅ Created WRITER user: writer / writer123");

        System.out.println("\n🎯 Default users created successfully!");
    }
}
