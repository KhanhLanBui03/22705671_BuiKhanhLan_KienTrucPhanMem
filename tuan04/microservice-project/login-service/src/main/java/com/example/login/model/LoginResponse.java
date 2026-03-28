package com.example.login.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private String email;

    public LoginResponse() {}

    private LoginResponse(Builder b) {
        this.success  = b.success;
        this.message  = b.message;
        this.token    = b.token;
        this.username = b.username;
        this.email    = b.email;
    }

    public boolean isSuccess()  { return success; }
    public String getMessage()  { return message; }
    public String getToken()    { return token; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }

    public void setSuccess(boolean v)  { this.success = v; }
    public void setMessage(String v)   { this.message = v; }
    public void setToken(String v)     { this.token = v; }
    public void setUsername(String v)  { this.username = v; }
    public void setEmail(String v)     { this.email = v; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private boolean success;
        private String message, token, username, email;

        public Builder success(boolean v)  { success = v;  return this; }
        public Builder message(String v)   { message = v;  return this; }
        public Builder token(String v)     { token = v;    return this; }
        public Builder username(String v)  { username = v; return this; }
        public Builder email(String v)     { email = v;    return this; }
        public LoginResponse build() { return new LoginResponse(this); }
    }
}
