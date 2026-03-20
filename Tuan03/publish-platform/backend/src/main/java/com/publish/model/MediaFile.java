package com.publish.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media_files")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName; // tên file trên disk

    @Column(nullable = false)
    private String url; // đường dẫn truy cập

    @Column(nullable = false)
    private String mimeType;

    private Long fileSize; // bytes

    @Enumerated(EnumType.STRING)
    private MediaType mediaType; // IMAGE, VIDEO, DOCUMENT

    private String uploadedBy;

    @Column(updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public enum MediaType { IMAGE, VIDEO, DOCUMENT }

    // Getters / Setters
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getOriginalName()            { return originalName; }
    public void setOriginalName(String name)   { this.originalName = name; }
    public String getStoredName()              { return storedName; }
    public void setStoredName(String name)     { this.storedName = name; }
    public String getUrl()                     { return url; }
    public void setUrl(String url)             { this.url = url; }
    public String getMimeType()                { return mimeType; }
    public void setMimeType(String mimeType)   { this.mimeType = mimeType; }
    public Long getFileSize()                  { return fileSize; }
    public void setFileSize(Long fileSize)     { this.fileSize = fileSize; }
    public MediaType getMediaType()            { return mediaType; }
    public void setMediaType(MediaType t)      { this.mediaType = t; }
    public String getUploadedBy()              { return uploadedBy; }
    public void setUploadedBy(String u)        { this.uploadedBy = u; }
    public LocalDateTime getUploadedAt()       { return uploadedAt; }
}
