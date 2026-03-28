package com.example.login.model;

/**
 * Ánh xạ JSON trả về từ Register Service qua RestTemplate
 */
public class UserInfo {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;

    public UserInfo() {}

    public Long getId()         { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail()    { return email; }
    public String getFullName() { return fullName; }

    public void setId(Long v)         { this.id = v; }
    public void setUsername(String v) { this.username = v; }
    public void setPassword(String v) { this.password = v; }
    public void setEmail(String v)    { this.email = v; }
    public void setFullName(String v) { this.fullName = v; }
}
