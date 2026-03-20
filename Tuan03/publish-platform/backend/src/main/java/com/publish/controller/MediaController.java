package com.publish.controller;

import com.publish.model.MediaFile;
import com.publish.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// ── Presentation Layer: POST /media ──────────────────────────────
@RestController
@RequestMapping("/api")
public class MediaController {

    private final MediaService mediaService;

    @Value("${media.upload-dir:uploads}")
    private String uploadDir;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // POST /api/media — upload file
    @PostMapping(value = "/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaFile> upload(@RequestParam("file") MultipartFile file,
                                             @RequestHeader("X-Username") String username) throws IOException {
        MediaFile media = mediaService.upload(file, username);
        return ResponseEntity.ok(media);
    }

    // DELETE /api/media/{id}
    @DeleteMapping("/media/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @RequestHeader("X-Username") String username) throws IOException {
        mediaService.delete(id, username);
        return ResponseEntity.noContent().build();
    }

    // GET /api/media — danh sách tất cả media
    @GetMapping("/media")
    public List<MediaFile> getAll() {
        return mediaService.getAll();
    }

    // GET /api/media/images — chỉ ảnh
    @GetMapping("/media/images")
    public List<MediaFile> getImages() {
        return mediaService.getImages();
    }

    // GET /api/media/mine — media của mình
    @GetMapping("/media/mine")
    public List<MediaFile> getMine(@RequestHeader("X-Username") String username) {
        return mediaService.getByUser(username);
    }

    // GET /files/{filename} — phục vụ file tĩnh
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws MalformedURLException {
        Path   filePath = Paths.get(uploadDir).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
            .body(resource);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException e) {
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }
}
