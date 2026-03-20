package com.publish.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String authorUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime publishedAt;

    public enum ArticleStatus { DRAFT, PUBLISHED, ARCHIVED }

    @PreUpdate
    public void preUpdate() { updatedAt = LocalDateTime.now(); }

    // Getters / Setters
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public String getTitle()                     { return title; }
    public void setTitle(String title)           { this.title = title; }
    public String getContent()                   { return content; }
    public void setContent(String content)       { this.content = content; }
    public String getAuthorUsername()            { return authorUsername; }
    public void setAuthorUsername(String u)      { this.authorUsername = u; }
    public ArticleStatus getStatus()             { return status; }
    public void setStatus(ArticleStatus status)  { this.status = status; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public LocalDateTime getUpdatedAt()          { return updatedAt; }
    public LocalDateTime getPublishedAt()        { return publishedAt; }
    public void setPublishedAt(LocalDateTime t)  { this.publishedAt = t; }
}
