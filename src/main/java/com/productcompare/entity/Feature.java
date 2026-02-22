package com.productcompare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feature", uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "name"}))
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer importance = 1;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private UserWorkspace workspace;

    public Feature() {
    }

    public Feature(Long id, String name, Integer importance, UserWorkspace workspace) {
        this.id = id;
        this.name = name;
        this.importance = importance;
        this.workspace = workspace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public UserWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(UserWorkspace workspace) {
        this.workspace = workspace;
    }
}
