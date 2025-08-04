package com.learnspherel.exception;

public class BaiHocNotFoundException extends RuntimeException {
    public BaiHocNotFoundException(String message) {
        super(message);
    }
}