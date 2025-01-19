package com.opan.urlshortener.services;

import com.opan.urlshortener.entities.ShortUrl;
import com.opan.urlshortener.exceptions.IdAlreadyExistsException;
import com.opan.urlshortener.exceptions.UrlNotFoundException;
import com.opan.urlshortener.repositories.ShortUrlRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ID_LENGTH = 6;

    private final ShortUrlRepository repository;
    private final Random random = new Random();

    public ShortUrl createShortUrl(String longUrl, String customId, Integer ttlHours) {
        String id = customId != null ? customId : generateId();

        System.out.println("customId: " + customId);
        if (customId != null && repository.existsById(customId)) {
            logger.error("Custom ID {} already exists", customId);
            throw new IdAlreadyExistsException("Custom ID already exists");
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(id);
        shortUrl.setLongUrl(longUrl);
        shortUrl.setCreatedAt(LocalDateTime.now());

        if (ttlHours != null) {
            shortUrl.setExpiresAt(LocalDateTime.now().plusHours(ttlHours));
        }

        logger.info("Creating short URL with ID: {}", id);
        return repository.save(shortUrl);
    }

    public ShortUrl getUrl(String id) {
        return repository.findById(id)
                .map(url -> {
                    if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
                        repository.delete(url);
                        logger.info("Deleted expired URL with ID: {}", id);
                        throw new UrlNotFoundException("URL has expired and been removed");
                    }
                    return url;
                })
                .orElseThrow(() -> {
                    logger.error("URL with ID {} not found", id);
                    return new UrlNotFoundException("URL not found");
                });
    }

    public void deleteUrl(String id) {
        if (!repository.existsById(id)) {
            logger.error("URL with ID {} not found for deletion", id);
            throw new UrlNotFoundException("URL not found");
        }
        logger.info("Deleting URL with ID: {}", id);
        repository.deleteById(id);
    }

    private String generateId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredUrls() {
        List<ShortUrl> expiredUrls = repository.findExpiredUrls(LocalDateTime.now());
        repository.deleteAll(expiredUrls);
        logger.info("Cleaned up {} expired URLs", expiredUrls.size());
    }
}