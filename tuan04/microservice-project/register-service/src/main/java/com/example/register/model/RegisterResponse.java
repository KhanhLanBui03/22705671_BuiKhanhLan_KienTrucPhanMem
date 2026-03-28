package com.example.register.model;

import java.time.LocalDateTime;

public class RegisterResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public RegisterResponse() {}

    private RegisterResponse(Builder b) {
        this.success   = b.success;
        this.message   = b.message;
        this.userId    = b.userId;
        this.username  = b.username;
        this.email     = b.email;
        this.createdAt = b.createdAt;
    }

    public boolean isSuccess()          { return success; }
    public String getMessage()          { return message; }
    public Long getUserId()             { return userId; }
    public String getUsername()         { return username; }
    public String getEmail()            { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSuccess(boolean success)           { this.success = success; }
    public void setMessage(String message)            { this.message = message; }
    public void setUserId(Long userId)                { this.userId = userId; }
    public void setUsername(String username)          { this.username = username; }
    public void setEmail(String email)                { this.email = email; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private boolean success;
        private String message, username, email;
        private Long userId;
        private LocalDateTime createdAt;

        public Builder success(boolean v)         { success = v;   return this; }
        public Builder message(String v)          { message = v;   return this; }
        public Builder userId(Long v)             { userId = v;    return this; }
        public Builder username(String v)         { username = v;  return this; }
        public Builder email(String v)            { email = v;     return this; }
        public Builder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public RegisterResponse build() { return new RegisterResponse(this); }
    }
}
