package com.productcompare.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_session")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private UserWorkspace workspace;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastAccessAt;

    public UserSession() {
    }

    public UserSession(Long id, String token, UserWorkspace workspace, LocalDateTime createdAt, LocalDateTime lastAccessAt) {
        this.id = id;
        this.token = token;
        this.workspace = workspace;
        this.createdAt = createdAt;
        this.lastAccessAt = lastAccessAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(UserWorkspace workspace) {
        this.workspace = workspace;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public void setLastAccessAt(LocalDateTime lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
    }
}
