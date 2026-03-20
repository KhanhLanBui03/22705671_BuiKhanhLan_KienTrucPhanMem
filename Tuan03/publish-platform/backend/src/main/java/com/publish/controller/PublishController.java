package com.publish.controller;

import com.publish.model.Article;
import com.publish.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ── Presentation Layer: Publish API ──────────────────────────────
@RestController
@RequestMapping("/api")
public class PublishController {

    private final ArticleService articleService;

    public PublishController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // POST /api/articles — tạo bài viết nháp
    @PostMapping("/articles")
    public ResponseEntity<Article> createDraft(@RequestBody Map<String, String> body,
                                                @RequestHeader("X-Username") String username) {
        Article article = articleService.createDraft(
            body.get("title"),
            body.get("content"),
            username
        );
        return ResponseEntity.ok(article);
    }

    // PUT /api/articles/{id} — cập nhật bài viết
    @PutMapping("/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           @RequestHeader("X-Username") String username) {
        Article article = articleService.update(id, body.get("title"), body.get("content"), username);
        return ResponseEntity.ok(article);
    }

    // POST /api/publish/{id} — đăng bài (DRAFT → PUBLISHED)
    @PostMapping("/publish/{id}")
    public ResponseEntity<Article> publish(@PathVariable Long id,
                                            @RequestHeader("X-Username") String username) {
        Article article = articleService.publish(id, username);
        return ResponseEntity.ok(article);
    }

    // POST /api/archive/{id} — archive bài viết
    @PostMapping("/archive/{id}")
    public ResponseEntity<Article> archive(@PathVariable Long id,
                                            @RequestHeader("X-Username") String username) {
        Article article = articleService.archive(id, username);
        return ResponseEntity.ok(article);
    }

    // DELETE /api/articles/{id}
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @RequestHeader("X-Username") String username) {
        articleService.delete(id, username);
        return ResponseEntity.noContent().build();
    }

    // GET /api/articles — tất cả bài viết (ADMIN/EDITOR)
    @GetMapping("/articles")
    public List<Article> getAll() {
        return articleService.getAll();
    }

    // GET /api/articles/published — bài đã đăng (public)
    @GetMapping("/articles/published")
    public List<Article> getPublished() {
        return articleService.getPublished();
    }

    // GET /api/articles/mine — bài của mình
    @GetMapping("/articles/mine")
    public List<Article> getMine(@RequestHeader("X-Username") String username) {
        return articleService.getByAuthor(username);
    }

    // GET /api/articles/{id}
    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    // ── Global error handler ──────────────────────────────────────

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException e) {
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }
}
