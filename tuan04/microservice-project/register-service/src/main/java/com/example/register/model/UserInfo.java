package com.example.register.model;

public class UserInfo {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;

    public UserInfo() {}

    private UserInfo(Builder b) {
        this.id       = b.id;
        this.username = b.username;
        this.password = b.password;
        this.email    = b.email;
        this.fullName = b.fullName;
    }

    public Long getId()         { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail()    { return email; }
    public String getFullName() { return fullName; }

    public void setId(Long id)                { this.id = id; }
    public void setUsername(String username)  { this.username = username; }
    public void setPassword(String password)  { this.password = password; }
    public void setEmail(String email)        { this.email = email; }
    public void setFullName(String fullName)  { this.fullName = fullName; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username, password, email, fullName;

        public Builder id(Long v)         { id = v;       return this; }
        public Builder username(String v) { username = v; return this; }
        public Builder password(String v) { password = v; return this; }
        public Builder email(String v)    { email = v;    return this; }
        public Builder fullName(String v) { fullName = v; return this; }
        public UserInfo build() { return new UserInfo(this); }
    }
}
