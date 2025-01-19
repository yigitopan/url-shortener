package com.opan.urlshortener.repositories;

import com.opan.urlshortener.entities.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, String> {
    @Query("SELECT s FROM ShortUrl s WHERE s.expiresAt < :now")
    List<ShortUrl> findExpiredUrls(LocalDateTime now);
}