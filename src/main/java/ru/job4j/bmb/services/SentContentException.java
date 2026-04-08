package ru.job4j.bmb.services;

public class SentContentException extends RuntimeException {
    public SentContentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
