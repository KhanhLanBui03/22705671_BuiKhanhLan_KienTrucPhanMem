package com.publish.repository;

import com.publish.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ─── ArticleRepository ────────────────────────────────────────────
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorUsername(String username);

    List<Article> findByStatus(Article.ArticleStatus status);

    List<Article> findByAuthorUsernameAndStatus(String username, Article.ArticleStatus status);
}
