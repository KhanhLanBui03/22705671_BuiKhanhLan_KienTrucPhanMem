package com.publish.service;

import com.publish.model.Article;
import com.publish.model.User;
import com.publish.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// ── Business Logic Layer: Xử lý bài viết ─────────────────────────
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthService       authService;

    public ArticleService(ArticleRepository articleRepository, AuthService authService) {
        this.articleRepository = articleRepository;
        this.authService       = authService;
    }

    // ── Tạo bài viết nháp ────────────────────────────────────────

    public Article createDraft(String title, String content, String authorUsername) {
        validate(title, content);

        Article article = new Article();
        article.setTitle(title.trim());
        article.setContent(content.trim());
        article.setAuthorUsername(authorUsername);
        article.setStatus(Article.ArticleStatus.DRAFT);
        return articleRepository.save(article);
    }

    // ── Cập nhật bài viết ────────────────────────────────────────

    public Article update(Long id, String title, String content, String requestedBy) {
        Article article = getOrThrow(id);
        checkOwnerOrEditor(article, requestedBy);
        validate(title, content);

        article.setTitle(title.trim());
        article.setContent(content.trim());
        return articleRepository.save(article);
    }

    // ── Đăng bài (DRAFT → PUBLISHED) ────────────────────────────
    // Kiểm tra trạng thái + quyền trước khi publish

    public Article publish(Long id, String requestedBy) {
        Article article = getOrThrow(id);
        User    user    = authService.findByUsername(requestedBy);

        // Chỉ ADMIN/EDITOR mới được publish
        if (!authService.canPublish(user))
            throw new SecurityException("Bạn không có quyền đăng bài");

        if (article.getStatus() == Article.ArticleStatus.PUBLISHED)
            throw new IllegalStateException("Bài viết đã được đăng rồi");

        article.setStatus(Article.ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    // ── Archive ──────────────────────────────────────────────────

    public Article archive(Long id, String requestedBy) {
        Article article = getOrThrow(id);
        User    user    = authService.findByUsername(requestedBy);

        if (!authService.canPublish(user))
            throw new SecurityException("Không có quyền archive bài viết");

        article.setStatus(Article.ArticleStatus.ARCHIVED);
        return articleRepository.save(article);
    }

    // ── Xóa ──────────────────────────────────────────────────────

    public void delete(Long id, String requestedBy) {
        Article article = getOrThrow(id);
        checkOwnerOrEditor(article, requestedBy);
        articleRepository.delete(article);
    }

    // ── Queries ───────────────────────────────────────────────────

    public List<Article> getAll() {
        return articleRepository.findAll();
    }

    public List<Article> getPublished() {
        return articleRepository.findByStatus(Article.ArticleStatus.PUBLISHED);
    }

    public List<Article> getByAuthor(String username) {
        return articleRepository.findByAuthorUsername(username);
    }

    public Article getById(Long id) {
        return getOrThrow(id);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Article getOrThrow(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại: " + id));
    }

    private void validate(String title, String content) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Tiêu đề không được rỗng");
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Nội dung không được rỗng");
        if (title.length() > 200)
            throw new IllegalArgumentException("Tiêu đề tối đa 200 ký tự");
    }

    private void checkOwnerOrEditor(Article article, String requestedBy) {
        User user = authService.findByUsername(requestedBy);
        boolean isOwner  = article.getAuthorUsername().equals(requestedBy);
        boolean isEditor = authService.canPublish(user);
        if (!isOwner && !isEditor)
            throw new SecurityException("Bạn không có quyền sửa bài viết này");
    }
}
