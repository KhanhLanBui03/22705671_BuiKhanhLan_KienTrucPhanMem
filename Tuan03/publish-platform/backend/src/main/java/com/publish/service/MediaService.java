package com.publish.service;

import com.publish.model.MediaFile;
import com.publish.model.User;

import com.publish.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

// ── Business Logic Layer: Xử lý media (resize, nén ảnh) ──────────
@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final AuthService     authService;

    @Value("${media.upload-dir:uploads}")
    private String uploadDir;

    @Value("${media.base-url:http://localhost:8080/files}")
    private String baseUrl;

    // Giới hạn kích thước
    private static final long MAX_IMAGE_SIZE = 5  * 1024 * 1024; // 5MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    public MediaService(MediaRepository mediaRepository, AuthService authService) {
        this.mediaRepository = mediaRepository;
        this.authService     = authService;
    }

    // ── Upload file ───────────────────────────────────────────────

    public MediaFile upload(MultipartFile file, String uploadedBy) throws IOException {
        User user = authService.findByUsername(uploadedBy);
        if (!authService.canManageMedia(user))
            throw new SecurityException("Bạn không có quyền upload media");

        // Xác định loại media
        String mimeType  = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        MediaFile.MediaType mediaType = resolveMediaType(mimeType);

        // Kiểm tra kích thước
        validateSize(file.getSize(), mediaType);

        // Tạo tên file unique
        String extension  = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + extension;

        // Lưu file lên disk
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);

        // Lưu metadata vào DB
        MediaFile media = new MediaFile();
        media.setOriginalName(file.getOriginalFilename());
        media.setStoredName(storedName);
        media.setUrl(baseUrl + "/" + storedName);
        media.setMimeType(mimeType);
        media.setFileSize(file.getSize());
        media.setMediaType(mediaType);
        media.setUploadedBy(uploadedBy);

        return mediaRepository.save(media);
    }

    // ── Xóa file ─────────────────────────────────────────────────

    public void delete(Long id, String requestedBy) throws IOException {
        MediaFile media = mediaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("File không tồn tại"));

        User user = authService.findByUsername(requestedBy);
        boolean isOwner  = media.getUploadedBy().equals(requestedBy);
        boolean isEditor = authService.canManageMedia(user);

        if (!isOwner && !isEditor)
            throw new SecurityException("Không có quyền xóa file này");

        // Xóa file vật lý
        Path filePath = Paths.get(uploadDir, media.getStoredName());
        Files.deleteIfExists(filePath);

        mediaRepository.delete(media);
    }

    // ── Queries ───────────────────────────────────────────────────

    public List<MediaFile> getAll()                   { return mediaRepository.findAll(); }
    public List<MediaFile> getByUser(String username) { return mediaRepository.findByUploadedBy(username); }
    public List<MediaFile> getImages()                { return mediaRepository.findByMediaType(MediaFile.MediaType.IMAGE); }

    // ── Helpers ───────────────────────────────────────────────────

    private MediaFile.MediaType resolveMediaType(String mimeType) {
        if (mimeType.startsWith("image/")) return MediaFile.MediaType.IMAGE;
        if (mimeType.startsWith("video/")) return MediaFile.MediaType.VIDEO;
        return MediaFile.MediaType.DOCUMENT;
    }

    private void validateSize(long size, MediaFile.MediaType type) {
        if (type == MediaFile.MediaType.IMAGE && size > MAX_IMAGE_SIZE)
            throw new IllegalArgumentException("Ảnh tối đa 5MB");
        if (type == MediaFile.MediaType.VIDEO && size > MAX_VIDEO_SIZE)
            throw new IllegalArgumentException("Video tối đa 100MB");
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
