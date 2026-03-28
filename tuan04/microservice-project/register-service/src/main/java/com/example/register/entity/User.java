package com.example.register.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {}

    private User(Builder b) {
        this.username = b.username;
        this.password = b.password;
        this.email    = b.email;
        this.fullName = b.fullName;
    }

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public Long getId()                 { return id; }
    public String getUsername()         { return username; }
    public String getPassword()         { return password; }
    public String getEmail()            { return email; }
    public String getFullName()         { return fullName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id)                       { this.id = id; }
    public void setUsername(String username)         { this.username = username; }
    public void setPassword(String password)         { this.password = password; }
    public void setEmail(String email)               { this.email = email; }
    public void setFullName(String fullName)         { this.fullName = fullName; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String username, password, email, fullName;
        public Builder username(String v) { username = v; return this; }
        public Builder password(String v) { password = v; return this; }
        public Builder email(String v)    { email = v;    return this; }
        public Builder fullName(String v) { fullName = v; return this; }
        public User build() { return new User(this); }
    }
}
