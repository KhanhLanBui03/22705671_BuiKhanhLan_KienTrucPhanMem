package com.publish.repository;

import com.publish.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ─── MediaRepository ─────────────────────────────────────────────
@Repository
public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByUploadedBy(String username);

    List<MediaFile> findByMediaType(MediaFile.MediaType type);
}
