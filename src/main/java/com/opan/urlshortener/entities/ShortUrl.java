package com.opan.urlshortener.entities;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "short_urls")
public class ShortUrl {
    @Id
    private String id;

    @Column(nullable = false)
    private String longUrl;

    @Column
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}