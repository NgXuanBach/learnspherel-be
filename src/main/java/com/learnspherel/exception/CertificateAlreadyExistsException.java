package com.learnspherel.exception;

public class CertificateAlreadyExistsException extends RuntimeException {
    public CertificateAlreadyExistsException(String msg) { super(msg); }
}