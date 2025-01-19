package com.opan.urlshortener.controllers;

import com.opan.urlshortener.dto.CreateUrlRequest;
import com.opan.urlshortener.entities.ShortUrl;
import com.opan.urlshortener.services.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlShortenerService service;

    @PostMapping("/api/urls")
    public ResponseEntity<ShortUrl> createShortUrl(@RequestBody CreateUrlRequest request) {
        return ResponseEntity.ok(service.createShortUrl(
                request.getLongUrl(),
                request.getCustomId(),
                request.getTtlHours()
        ));
    }

    @GetMapping("/{id}")
    public void redirectToLongUrl(@PathVariable String id, HttpServletResponse response) throws IOException {
        ShortUrl shortUrl = service.getUrl(id);
        response.sendRedirect(shortUrl.getLongUrl());
    }

    @DeleteMapping("/api/urls/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShortUrl(@PathVariable String id) {
        service.deleteUrl(id);
    }
}