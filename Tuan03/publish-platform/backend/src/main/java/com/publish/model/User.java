package com.publish.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.WRITER;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        ADMIN,    // toàn quyền
        EDITOR,   // duyệt + sửa bài
        WRITER    // chỉ viết bài của mình
    }

    // Getters / Setters
    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }
    public String getPasswordHash()          { return passwordHash; }
    public void setPasswordHash(String p)    { this.passwordHash = p; }
    public Role getRole()                    { return role; }
    public void setRole(Role role)           { this.role = role; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
}
