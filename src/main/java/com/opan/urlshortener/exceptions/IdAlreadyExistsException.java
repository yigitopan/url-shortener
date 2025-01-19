package com.opan.urlshortener.exceptions;
public class IdAlreadyExistsException extends RuntimeException {
    public IdAlreadyExistsException(String message) {
        super(message);
    }
}