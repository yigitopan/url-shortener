package com.opan.urlshortener.dto;

import lombok.Data;

@Data
public class CreateUrlRequest {
    private String longUrl;
    private String customId;
    private Integer ttlHours;
}