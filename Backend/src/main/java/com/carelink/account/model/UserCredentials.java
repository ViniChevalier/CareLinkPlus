package com.carelink.account.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "usercredentials")
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer credentialID;

    @OneToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 255)
    private String passwordSalt;

    @Column
    private Timestamp lastLogin;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "token_expiration")
    private Timestamp tokenExpiration;

    // Getters and Setters
    public Integer getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(Integer credentialID) {
        this.credentialID = credentialID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }
    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }
    public Timestamp getTokenExpiration() {
        return tokenExpiration;
    }
    public void setTokenExpiration(Timestamp tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }
    
}